import React from 'react-native'
import {DeviceEventEmitter} from 'react-native'
import { NativeModules } from 'react-native';
const { RNRichNotification } = NativeModules;
import DeviceInfo from 'react-native-device-info';



export enum EventType {
    onNotificationTap = 'onNotificationTap'
}

export default class RichNotification {
    /**
     * 监听点击通知栏消息的回调，在打开app时会收到该回调
     * 
     */
    static addNotificationTapListener(callback):React.EmitterSubscription {
        return DeviceEventEmitter.addListener(EventType.onNotificationTap,(message)=>{
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
    static getRegisterId(callback){
        RNRichNotification.getRegisterId(callback);
    }


    /**
     * 初始化推送服务, 必须先初始化后再调用其它方法
     * @param callback 
     */
    static initPush(callback?){
        console.warn(DeviceInfo.getBrand());
    }
}

