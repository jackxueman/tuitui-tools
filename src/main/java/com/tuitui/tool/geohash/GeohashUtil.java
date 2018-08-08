package com.tuitui.tool.geohash;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.queries.GeoHashCircleQuery;
import com.google.common.base.MoreObjects;
import com.tuitui.tool.gps.GPSUtil;

import java.util.*;

/**
 * Geohash工具类
 *
 * @author liujianxue
 * @since 2018/2/28
 */
public final class GeohashUtil {

    private static final String BASE32_CODES = "0123456789bcdefghjkmnpqrstuvwxyz";

    private static HashMap<Character, Integer> BASE32_CODES_MAP;

    static {
        BASE32_CODES_MAP = new HashMap<>();
        for (int i = 0; i < BASE32_CODES.length(); i++) {
            BASE32_CODES_MAP.put(BASE32_CODES.charAt(i), i);
        }
    }

    private GeohashUtil() {
    }

    /**
     * geohash 转经纬度
     * lat纬度，lng经度
     * 数据大脑统一转成百度系坐标出去
     * @param geohash geohash
     *
     * @return 百度系经纬度
     */
    public static double[] decode(String geohash) {
        double[] bbox = decodeBox(geohash.trim());
        double lat = (bbox[0] + bbox[2]) / 2;
        double lng = (bbox[1] + bbox[3]) / 2;
        double latErr = bbox[2] - lat;
        double lngErr = bbox[3] - lng;
        double[] coord = new double[]{lat, lng, latErr, lngErr};

        return GPSUtil.gps84_To_bd09(coord[0], coord[1]);

    }

    /**
     * 计算 geohash 格子各点的经纬度
     *
     * @param geohash geohash
     * @return geohash 格子4个点的经纬度
     */
    protected static double[] decodeBox(String geohash) {
        geohash = geohash.toLowerCase();

        boolean isLng = true;

        double minLat = -90.0;
        double maxLat = 90.0;
        double minLng = -180.0;
        double maxLng = 180.0;
        double mid;

        for (int i = 0, l = geohash.length(); i < l; i++) {
            char code = geohash.charAt(i);
            int hashValue = BASE32_CODES_MAP.get(code);

            for (int bits = 4; bits >= 0; bits--) {
                int bit = (hashValue >> bits) & 1;
                if (isLng) {
                    mid = (maxLng + minLng) / 2;
                    if (bit == 1) {
                        minLng = mid;
                    } else {
                        maxLng = mid;
                    }
                } else {
                    mid = (maxLat + minLat) / 2;
                    if (bit == 1) {
                        minLat = mid;
                    } else {
                        maxLat = mid;
                    }
                }
                isLng = !isLng;
            }
        }
        return new double[]{minLat, minLng, maxLat, maxLng};
    }

    /**
     * geohash 转百度经纬度
     *
     * @param geohash
     * @return
     */
    public static double[] decodeBaidu(String geohash) {
        double[] coord = decode(geohash);
        return GPSUtil.gps84_To_bd09(coord[0], coord[1]);
    }

    /**
     * 获取圆区域geohashes
     * @param lat
     * @param lng
     * @param radius
     * @param precision
     * @return
     */
    public static Set<String> getSearchHashes(double lat,double lng,int radius,int precision){

        GeoHashCircleQuery q = new GeoHashCircleQuery(new WGS84Point(lat, lng), MoreObjects.firstNonNull(radius,90));
        GeoHash center = GeoHash.withCharacterPrecision(lat, lng, MoreObjects.firstNonNull(precision,8));

        Set<String> hashKeys = new HashSet<>(8);
        Set<GeoHash> seen = new HashSet<>(8);
        Queue<GeoHash> candidates = new LinkedList<>();
        candidates.add(center);
        while (!candidates.isEmpty()) {
            GeoHash gh = candidates.remove();
            hashKeys.add(gh.toBase32());

            GeoHash[] neighbors = gh.getAdjacent();
            for (GeoHash neighbor : neighbors) {
                if (seen.add(neighbor) && q.contains(neighbor)) {
                    candidates.add(neighbor);
                }
            }
        }

        return hashKeys;
    }
}
