import React from 'react-native'
import {DeviceEventEmitter} from 'react-native'
import { NativeModules } from 'react-native';
const { RNRichNotification } = NativeModules;
import DeviceInfo from 'react-native-device-info';
import { DeviceRegisterInfo, getRegisterIdCallback, initPushCall, initPushCallback } from 'react-native-rich-notification';



export enum EventType {
    /**
     * 监听点击通知栏事件
     */
    onNotificationTap = 'onNotificationTap',
    /**
     * 监听华为透传消息事件
     */
    onHuaWeiRemoteMessage = "onHuaWeiRemoteMessage"
}

export default class RichNotification {
    /**
     * 监听点击通知栏消息的行为，目前只支持点击通知栏消息唤起app，
     * 后续跳转app的其他页面可以根据回调中的参数处理
     * 
     */
    static addNotificationTapListener(callback):React.EmitterSubscription {
        return DeviceEventEmitter.addListener(EventType.onNotificationTap,(message)=>{
            callback&&callback(message)
        })
    }
    

    /**
     * 监听华为透传消息
     * @param callback 
     * @returns 
     */
    static addHuaweiRemoteMessageListener(callback):React.EmitterSubscription {
        return DeviceEventEmitter.addListener(EventType.onHuaWeiRemoteMessage,(message)=>{
            callback&&callback(message)
        })
    }

    /**
     * 移除事件监听
     */

    static removeListener(listener:React.EmitterSubscription){
        listener&&listener.remove()
    }

    /**
     * 获取注册id
     */
    static getRegisterId(callback:getRegisterIdCallback){
        RNRichNotification.getRegisterId(callback);
    }


    /**
     * 初始化推送服务, 必须先初始化后再调用其它方法
     * 不同厂商需要不同的参数，不用都传
     * @param appId 厂商平台注册的appid
     * @param appKey 厂商平台注册的appKey  
     * @param appSecret 厂商平台注册的appSecret
     * @param callback 结果回调
     */
    static initPush(appId:string,appKey:string,appSecret:string,callback?:initPushCall){               
        const _appId= appId??""
        const _appKey = appKey??""
        const _appSecret = appSecret??""
        RNRichNotification.initPush(_appId, _appKey,_appSecret,callback)
    }
}

