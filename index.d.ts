export default  class RichNotification {
    /**
     * 监听点击通知栏消息回调事件，在点击通知栏消息后会触发
     * @param callback 
     */
    static addNotificationTapListener(callback):React.EmitterSubscription
    
    /**
     * 移除监听事件
     * @param listener 
     */
    static removeListener(listener:React.EmitterSubscription)


    /**
     * 获取本机在本厂商推送的设备注册ID或者是设备token
     * 不同厂商叫法不一样
     * @param callback 
     */
    static getRegisterId(callback)


    /**
     * 初始化推送服务, 必须先初始化后再调用其它方法
     * @param callback 
     */
    static initPush(callback)
    
    
}