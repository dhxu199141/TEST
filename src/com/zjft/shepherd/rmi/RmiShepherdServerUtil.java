/**
 * Created On 2009-03-18 By ykliu
 */
package com.zjft.shepherd.rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;


/**
 * @since 2009-03-19
 * @author ykliu 此接口用于定义本地和远程服务器上程序的公共接口
 */
public interface RmiShepherdServerUtil extends Remote {
	/**
	 * 浏览磁盘或文件夹
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备表 devInfo
	 *      文件路径 viewpath
	 *      文件名 docName
	 *      上级目录请求 upDoc
	 *      文件目录 dirPath
	 *      Remote业务监听端口 remotePort
	 *      Remote文件监听端口 remoteFilePort
	 *      当前用户 user;
	 * @return HashMap
	 * key:documentFileList
		   rHashMap.put("retCode", 1);
		   rHashMap.put("viewpath", viewpath);
	 **/
	Map<String, Object> viewDocFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 下载ATM上的文件到操作员端
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key：设备表 devInfo;
	 *  文件路径 viewpath;
	 *  文件名 fileName;
	 *  fileBean
	 *  文件目录 versionFilePath
	 *  Remote文件监听端口 remoteFilePort
	 *  当前用户 user;
	 * @return rHashMap
	 *  key：返回码 retCode 0或1;
	 *  返回信息 retMsg;
	 * */
	Map<String, Object> downloadFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 下载多个客户端文件到操作员端
	 * @author limengrd
	 * @since 2016.09.07
	 * @param pHashMap 
	 *	key:
	 *		Remote文件监听端口 remoteFilePort
	 *		当前用户 user;
	 *		每包传输大小 packetLen;
	 *		压缩方式 vzipType;
	 *		设备ip ipAddress;
	 *		设备号 devNo;
	 *      路径文件 pathType logPath&file#logPath&file
	 * @return rHashMap
	 *	key:返回码 retCode 0或1;
	 *		返回信息 retMsg;
	 * */
	Map<String, Object> downloadClientFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 上传文件到ATM设备
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key：设备表 devInfo;
	 *  文件路径 viewpath;
	 *  长传文件名 uploadedFileName;
	 *  上传方式 uploadType
	 *  Remote业务监听端口 remotePort;
	 *  当前用户 user;
	 * @return rHashMap
	 *  key：返回码 retCode 0或1;
	 *  返回信息 retMsg;
	 * */
	Map<String, Object> uploadFile(Map<String, Object> pHashMap) throws RemoteException;
	
	
	/**
	 * 上传文件到ATM设备
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key：设备表 devInfo;
	 *  文件路径 remoteFile;
	 *  Remote业务监听端口 remotePort;
	 *  当前用户 user;
	 * @return rHashMap
	 *  key：返回码 retCode 0或1;
	 *  返回信息 retMsg;
	 * */
	Map<String, Object> delFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 上传文件到ATM设备
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key：设备表 devInfo;
	 *  文件路径 remoteFile;
	 *  Remote业务监听端口 remotePort;
	 *  当前用户 user;
	 * @return rHashMap
	 *  key：返回码 retCode 0或1;
	 *  返回信息 retMsg;
	 * */
	Map<String, Object> excuteFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 执行执行命令
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备号数组 devInfo;
	 *  	命令 cmdId
	 *  	是否需要获取文件标志 fileFlag
	 *  	Remote业务监听端口 remotePort;
	 *  	Remote文件监听端口 remoteFilePort;
	 *  	v端文件存放路径 dirPath
	 *  	日志日期 logDate
	 *      当前用户 user;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	结果集 resultList
	 * */
	Map<String, Object> remoteControl(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 创建或者更新ATMC的功能信息
	 * 
	 * @param pHashMap
	 * @return rHashMap
	 * @throws RemoteException
	 */
	Map<String, Object> createOrUpdateAtmFunction(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 创建或者更新ATMC的日志补打池信息
	 * 
	 * @param pHashMap
	 * @return rHashMap
	 * @throws RemoteException
	 */
	Map<String, Object> createOrUpdateAtmLog(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 获取设备的功能列表信息
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备号 devNo;
	 *  	设备IP ip
	 *  	参数类型 paramtype
	 *  	参数类型值 paramtypeValue;
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	参数 param
	 * */
	Map<String, Object> getAtmParam(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 
	 * @param 获取复合卡状态
	 * @return
	 * @throws RemoteException
	 */
	Map<String, Object> getCompositeCardStatus(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 获取获取设备钞箱信息
	 * @author cy
	 * @since 2011-8-3
	 * @param pHashMap 
	 *  key:设备号 devNo;
	 *  	设备IP ip
	 *  	Remote业务监听端口 remotePort;
	 *      传输包大小
	 *      压缩方式
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	钞箱信息
	 * */
	Map<String, Object> getCashboxInfo(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 保存获取设备钞箱信息
	 * @author cy
	 * @since 2011-8-3
	 * @param pHashMap 
	 *  key:设备号 devNo;
	 *  	设备IP ip
	 *  	Remote业务监听端口 remotePort;
	 *      传输包大小
	 *      压缩方式
	 *      钞箱信息
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg,retcode;
	 * */
	Map<String, Object> setCashboxInfo(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 更新昆仑银行复合卡开启关闭功能
	 * @author cy
	 * @since 2013-4-23
	 * @param pHashMap 
	 *  key:设备号 devNo;
	 *  	设备IP ip
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 *  	通讯每包传输大小 packetLen
	 *      通讯压缩方式 vzipType
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	参数 result
	 * */
	Map<String, Object> createOrUpdateCompositeCardStatus(Map<String, Object> pHashMap)throws RemoteException;
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--用户编号
	 *     devInfo--设备信息    设备编号|ip地址|通讯每包传输大小|压缩标志
	 * @return 00成功，99失败
	 */
	Map<String, Object> qryDepositId(Map<String, Object> paraMap) throws RemoteException;
	
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--用户编号
	 *     devInfo--设备信息    设备编号|ip地址|通讯每包传输大小|压缩标志
	 *     version--版本号
	 *     ids--id列表
	 * @return 00成功，99失败
	 */
	Map<String, Object> setDepositId(Map<String, Object> paraMap) throws RemoteException;
	
	/**
	 * 
	 * @param paraMap
	 * key devInfo--设备编号|ip地址|通讯每包传输大小|压缩标志
	 *     devInfo--设备信息    设备编号|ip地址|通讯每包传输大小|压缩标志
	 *     date--日期
	 *     time--时间
	 *     userid--用户
	 *     commandCache--是否需要C端进行缓存处理
	 *     cimCountAll--是否精查所有钞箱
	 *     cimCashUnitCount--需要精查的钞箱数目
	 *     CashUnitList--逻辑钞箱索引号列表
	 *     checktype--精查方式，0-立即执行（默认），1-定时执行
	 * @return 00成功，99失败
	 */	
	Map<String, Object> qryCashBoxSift(Map<String, Object> paraMap) throws RemoteException;
	
	/**
	 * 获取设备端无纸化流水信息
	 */
	Map<String, Object> getCJournalProperties(Map<String, Object> pHashMap) throws RemoteException;

}
