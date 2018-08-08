package com.tuitui.tool.map;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.tuitui.tool.entity.CoordDTO;
import com.tuitui.tool.entity.LocationDTO;
import com.tuitui.tool.geohash.GeohashUtil;
import com.tuitui.tool.http.HttpAsyncClientUtil;
import com.tuitui.tool.http.HttpClientUtil;
import com.tuitui.tool.http.WebUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 百度地图工具类
 *
 * @author liujianxue
 * @since 2018/5/18
 */
@Component
public final class BaiduMapUtil {
    private static Logger logger = LoggerFactory.getLogger(BaiduMapUtil.class);

    private static final String OFFLINE_MODE = "offline";
    private static String offlineMapUrl;
    private static String onlineMapUrl;
    private static String ak;
    private static String mode;

    private BaiduMapUtil() {

    }

    /**
     * 请求百度接口
     *
     * @param geohash
     * @return
     */
    public static LocationDTO request(String geohash) {
        return request(Collections.singletonList(geohash)).get(0);
    }

    /**
     * 请求百度接口
     *
     * @param geohashList
     * @return
     */
    public static List<LocationDTO> request(List<String> geohashList) {
        if (CollectionUtils.isEmpty(geohashList)) {
            return Collections.emptyList();
        }
        geohashList = geohashList.stream().distinct().collect(Collectors.toList());

        if (OFFLINE_MODE.equalsIgnoreCase(mode)) {
            return requestOfflineService(geohashList);
        }
        return requestOnlineService(geohashList);
    }



    /**
     * List -> Map
     *
     * @param locationDTOList
     * @return
     */
    public static Map<String, LocationDTO> list2Map(List<LocationDTO> locationDTOList) {
        if (CollectionUtils.isEmpty(locationDTOList)) {
            return Collections.emptyMap();
        }
        return locationDTOList.stream().collect(Collectors.toMap(LocationDTO::getGeohash, Function.identity()));
    }

    /**
     * 请求离线地图服务
     *
     * @param geohashList
     * @return 经纬度都是百度系的
     */
    private static List<LocationDTO> requestOfflineService(List<String> geohashList) {
        if (CollectionUtils.isEmpty(geohashList)) {
            return Collections.emptyList();
        }

        String requestId = WebUtil.getRequestId();
        String request = JSONObject.toJSONString(ImmutableMap.of("geohashes", geohashList));
        String response = HttpClientUtil.post(offlineMapUrl, request);

        if (StringUtils.isEmpty(response)) {
            logger.info("当前请求的uri{}, 请求离线地图服务{}，空响应, 请求参数:{}", requestId, offlineMapUrl, request);
            return fillLocationDTOList(null, geohashList);
        }

        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject == null || !jsonObject.containsKey("code") || !jsonObject.containsKey("data")) {
            logger.info("当前请求的uri{}, 离线地图服务响应解析失败, 请求参数:{}，响应参数: {}", requestId, request, response);
            return fillLocationDTOList(null, geohashList);
        }

        if (!Integer.valueOf(0).equals(jsonObject.getInteger("code"))) {
            logger.info("当前请求的uri{}, 离线地图服务接口执行失败! 请求参数:{}，响应参数: {}", requestId, request, response);
            return fillLocationDTOList(null, geohashList);
        }

        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray == null || jsonArray.isEmpty()) {
            return fillLocationDTOList(null, geohashList);
        }
        return fillLocationDTOList(jsonArray.toJavaList(LocationDTO.class), geohashList);
    }

    /**
     * 请求在线地图服务
     *
     * @param geohashList
     * @return
     */
    private static List<LocationDTO> requestOnlineService(List<String> geohashList) {
        if (CollectionUtils.isEmpty(geohashList)) {
            return Collections.emptyList();
        }

        Map<String, String> requestMap = new HashMap<>(geohashList.size());
        geohashList.forEach(geohash -> requestMap.put(geohash, buildRequest(GeohashUtil.decode(geohash))));

        List<LocationDTO> locationDTOList = HttpAsyncClientUtil.batchGet(requestMap).entrySet().stream()
                .map(p -> parseBaiduMapResponse(p.getKey(), p.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return fillLocationDTOList(locationDTOList, geohashList);
    }

    /**
     * 解析百度地图响应
     *
     * @param geohash
     * @param response
     * @return
     */
    private static LocationDTO parseBaiduMapResponse(String geohash, String response) {
        if (StringUtils.isEmpty(response)) {
            return null;
        }

        Map<String, Object> responseMap = JSONObject.parseObject(response, Map.class);
        if (MapUtils.isEmpty(responseMap) || !Integer.valueOf(0).equals(responseMap.get("status"))) {
            return null;
        }

        Map<String, Object> resultMap = (Map<String, Object>) responseMap.get("result");
        if (MapUtils.isEmpty(resultMap)) {
            return null;
        }

        Map<String, Object> coordMap = (Map<String, Object>) resultMap.get("location");
        if (MapUtils.isEmpty(coordMap)) {
            return null;
        }
        double lat = Double.valueOf(coordMap.get("lat").toString());
        double lng = Double.valueOf(coordMap .get("lng").toString());

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocation(new CoordDTO(lat, lng));
        locationDTO.setAddress(String.valueOf(resultMap.get("formatted_address")));
        locationDTO.setGeohash(geohash);
        return locationDTO;
    }

    /**
     * 生成百度地图请求参数
     *
     * @param coord 经纬度
     * @return
     */
    private static String buildRequest(double[] coord) {
        return onlineMapUrl + "ak=" + ak + "&coordtype=wgs84ll&output=json&location=" + coord[0] + "," + coord[1];
    }

    /**
     * 构造 LocationDTO
     *
     * @param geohash
     * @return
     */
    private static LocationDTO buildLocationDTO(String geohash) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setGeohash(geohash);

        double[] coordinate = GeohashUtil.decode(geohash);
        locationDTO.setLocation(new CoordDTO(coordinate[0], coordinate[1]));
        locationDTO.setAddress("经度：" + coordinate[1] + "，纬度：" + coordinate[0]);
        return locationDTO;
    }

    /**
     * 补齐 LocationDTO
     *
     * @param locationDTOList
     * @param geohashList
     * @return
     */
    private static List<LocationDTO> fillLocationDTOList(List<LocationDTO> locationDTOList, List<String> geohashList) {
        if (CollectionUtils.isEmpty(geohashList)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(locationDTOList)) {
            return geohashList.stream().map(BaiduMapUtil::buildLocationDTO).collect(Collectors.toList());
        }

        List<String> originGeohashList = locationDTOList.stream()
                .map(LocationDTO::getGeohash)
                .collect(Collectors.toList());

        locationDTOList.addAll(CollectionUtils.subtract(geohashList, originGeohashList).stream()
                .map(BaiduMapUtil::buildLocationDTO)
                .collect(Collectors.toList()));
        return locationDTOList;
    }

    @Value("${baidu.map.offline.url}")
    public void setOfflineMapUrl(String offlineMapUrl) {
        BaiduMapUtil.offlineMapUrl = offlineMapUrl;
    }

    @Value("${baidu.map.online.url}")
    public void setOnlineMapUrl(String onlineMapUrl) {
        BaiduMapUtil.onlineMapUrl = onlineMapUrl;
    }

    @Value("${baidu.map.ak}")
    public void setAk(String ak) {
        BaiduMapUtil.ak = ak;
    }

    @Value("${baidu.map.mode}")
    public void setMode(String mode) {
        BaiduMapUtil.mode = mode;
    }

}
