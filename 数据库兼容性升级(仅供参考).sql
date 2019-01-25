-------涉及到RMI的数据库变动部分，需要在此处提交数据库提交脚本-----------
-------ORACLE脚本 2014-01-23 ---------------------------
-----【注：仅供参考，请勿直接使用，升级时，请逐一比对】--------------


-----------------------------V1.3.6版本开始2014-01-23 add by qfxu------------------------
--1、设备软件信息表(DEV_SOFTWARE_INFO)新增两个字段：SERIAL_NO_INFO_STATUS  INTEGER,
alter table DEV_SOFTWARE_INFO add (SERIAL_NO_INFO_STATUS  INTEGER);
alter table DEV_SOFTWARE_INFO add (TXT_INFO_STATUS  INTEGER);
comment on column DEV_SOFTWARE_INFO.SERIAL_NO_INFO_STATUS is '冠字号缓存开关状态（1:开启，0:关闭，其它：未知）';
comment on column DEV_SOFTWARE_INFO.TXT_INFO_STATUS is '现金缓存开关状态（1:开启，0:关闭，其它：未知）';
-----------------------------V1.3.6版本结束2014-02-08------------------------------------



-----------------------------V1.3.7版本开始 2014-02-11 add by qfxu------------------------
--设备软件信息表增加两个字段  需求来源：杭州银行    2014-02-11  add by qfxu
--设备基本信息表增加DEV_LOG_PATH，注意，DEV_LOG_PATH是一个绝对路径，并且包含文件名称及后缀  需求来源：杭州银行    2014-02-11  add by qfxu
--1、设备软件信息表(DEV_SOFTWARE_INFO)新增两个字段：
alter table DEV_SOFTWARE_INFO add (CIMSRP_FLAG  INTEGER);
alter table DEV_SOFTWARE_INFO add (CDMSRP_FLAG  INTEGER);
comment on column DEV_SOFTWARE_INFO.CIMSRP_FLAG is '存款冠字号凭条打印开关状态（1:开启，0:关闭，其它：未知）';
comment on column DEV_SOFTWARE_INFO.CDMSRP_FLAG is '取款冠字号凭条打印开关状态（1:开启，0:关闭，其它：未知）';
--2、设备基本信息表（DEV_BASE_INFO）新增字段：
alter table DEV_BASE_INFO add (DEV_LOG_PATH VARCHAR(200));
comment on column DEV_BASE_INFO.DEV_LOG_PATH is 'TCR设备日志路径（例如：C\:\\WSAP\\DATA\\yyyymmdd.J）';
-----------------------------V1.3.7版本结束 ------------------------------------------------


-----------------------------V1.4.0版本开始 2014-09-10 add by cqluo-------------------------
--1、设备基本信息表(DEV_BASE_INFO)新增字段：TRANS_RATE
--注意： 1、此sql语句如果需要进行速度限制时才执行，否则不执行
--    2、TRANS_RATE字段有可能已经存在，别的进程可能已经升级，执行前可以先确定此字段是否存在，避免重复执行报错
alter table DEV_BASE_INFO add (TRANS_RATE VARCHAR2(20));
comment on column DEV_BASE_INFO.TRANS_RATE is '文件上传下载最大速度,单位kb/s';
-----------------------------V1.4.0版本结束-------------------------------------------------

-----------------------------V1.4.1版本开始 2015-12-29 add by zhangdd------------------------
--兴业银行自动精查需求
--1、远程控制记录表(REMOTE_TRACE)新增字段：CACHE_FALG、COUNT_ALL_FLAG、CASHUNIT_COUNT、CASHUNIT_LIST
alter table REMOTE_TRACE add (CHECK_TYPE VARCHAR2(2));
comment on column REMOTE_TRACE.CHECK_TYPE is '精查方式，0-立即执行（默认），1-定时执行';
alter table REMOTE_TRACE add (CACHE_FALG VARCHAR2(2));
comment on column REMOTE_TRACE.CACHE_FALG is '是否需要C端进行缓存处理';
alter table REMOTE_TRACE add (COUNT_ALL_FLAG VARCHAR2(2));
comment on column REMOTE_TRACE.COUNT_ALL_FLAG is '是否精查所有钞箱';
alter table REMOTE_TRACE add (CASHUNIT_COUNT INTEGER);
comment on column REMOTE_TRACE.CASHUNIT_COUNT is '需要精查的钞箱数目';
alter table REMOTE_TRACE add (CASHUNIT_LIST VARCHAR2(100));
comment on column REMOTE_TRACE.CASHUNIT_LIST is '逻辑钞箱索引号列表';

--2、精查结果表(REMOTE_COUNT_RESULT_INFO)
create table REMOTE_COUNT_RESULT_INFO 
(
   LOGIC_ID                             VARCHAR2(36)         not null,
   TRACE_LOGIC_ID                  		VARCHAR2(36)         not null,
   COMM_RESULT                     		VARCHAR2(10),
   DATETIME                             VARCHAR2(20),
   COMM_START_TIME               		VARCHAR2(20),
   COMM_END_TIME                  		VARCHAR2(20),
   HOST_AMOUNT                        	INTEGER,
   LOCAL_AMOUNT                       	INTEGER,
   PUID_BEFORE                          VARCHAR2(100),
   PU_POS_NAME_BEFORE            		VARCHAR2(200),
   PU_REJECT_COUNT_BEFORE      			VARCHAR2(100),
   PU_COUNT_BEFORE                  	VARCHAR2(100),
   TYPE_BEFORE                          VARCHAR2(200),
   NOTE_VALUE_BEFORE               		VARCHAR2(100),
   STATUS_BEFORE                       	VARCHAR2(100),
   PUID_AFTER                           VARCHAR2(100),
   PU_POS_NAME_AFTER            		VARCHAR2(200),
   PU_REJECT_COUNT_AFTER      			VARCHAR2(100),
   PU_COUNT_AFTER                  		VARCHAR2(100),
   TYPE_AFTER                           VARCHAR2(200),
   NOTE_VALUE_AFTER               		VARCHAR2(100),
   STATUS_AFTER                       	VARCHAR2(100),
   constraint PK_RMT_COUNT_RESULT_INFO primary key (LOGIC_ID)
         using index pctfree 10
   initrans 2
   storage
   (
       initial 64K
       next 1024K
       minextents 1
       maxextents unlimited
   )
   logging
   tablespace IDXSPACE
)
pctfree 10
initrans 1
storage
(
    initial 64K
    next 1024K
    minextents 1
    maxextents unlimited
)
tablespace ATMVSPACE
logging
 nocompress
 monitoring
 noparallel;
      
comment on table REMOTE_COUNT_RESULT_INFO is
'精查结果表';

comment on column REMOTE_COUNT_RESULT_INFO.LOGIC_ID is
'编号';

comment on column REMOTE_COUNT_RESULT_INFO.TRACE_LOGIC_ID is
'远程控制结果编号';

comment on column REMOTE_COUNT_RESULT_INFO.COMM_RESULT is
'执行结果';

comment on column REMOTE_COUNT_RESULT_INFO.DATETIME is
'报文组织时间';

comment on column REMOTE_COUNT_RESULT_INFO.COMM_START_TIME is
'命令执行开始时间';

comment on column REMOTE_COUNT_RESULT_INFO.COMM_END_TIME is
'命令执行结束时间';

comment on column REMOTE_COUNT_RESULT_INFO.HOST_AMOUNT is
'尾箱金额';

comment on column REMOTE_COUNT_RESULT_INFO.LOCAL_AMOUNT is
'设备应有金额';

comment on column REMOTE_COUNT_RESULT_INFO.PUID_BEFORE is
'执行情况开始前的钞箱明细-物理钞箱ID集合';

comment on column REMOTE_COUNT_RESULT_INFO.PU_POS_NAME_BEFORE is
'执行情况开始前的钞箱明细-物理钞箱物理位置名集合';
 
comment on column REMOTE_COUNT_RESULT_INFO.PU_REJECT_COUNT_BEFORE is
'执行情况开始前的钞箱明细-物理钞箱拒钞张数集合';

comment on column REMOTE_COUNT_RESULT_INFO.PU_COUNT_BEFORE is
'执行情况开始前的钞箱明细-物理钞箱当前张数集合';

comment on column REMOTE_COUNT_RESULT_INFO.TYPE_BEFORE is
'执行情况开始前的钞箱明细-钞箱类型集合';

comment on column REMOTE_COUNT_RESULT_INFO.NOTE_VALUE_BEFORE is
'执行情况开始前的钞箱明细-面值集合';

 comment on column REMOTE_COUNT_RESULT_INFO.STATUS_BEFORE is
'执行情况开始前的钞箱明细-钞箱状态集合';  

comment on column REMOTE_COUNT_RESULT_INFO.PUID_AFTER is
'执行情况完成后的钞箱明细-物理钞箱ID集合';

comment on column REMOTE_COUNT_RESULT_INFO.PU_POS_NAME_AFTER is
'执行情况完成后的钞箱明细-物理钞箱物理位置名集合';
 
comment on column REMOTE_COUNT_RESULT_INFO.PU_REJECT_COUNT_AFTER is
'执行情况完成后的钞箱明细-物理钞箱拒钞张数集合';

comment on column REMOTE_COUNT_RESULT_INFO.PU_COUNT_AFTER is
'执行情况完成后的钞箱明细-物理钞箱当前张数集合';

comment on column REMOTE_COUNT_RESULT_INFO.TYPE_AFTER is
'执行情况完成后的钞箱明细-钞箱类型集合';

comment on column REMOTE_COUNT_RESULT_INFO.NOTE_VALUE_AFTER is
'执行情况完成后的钞箱明细-面值集合';

 comment on column REMOTE_COUNT_RESULT_INFO.STATUS_AFTER is
'执行情况完成后的钞箱明细-钞箱状态集合'; 
-----------------------------V1.4.1版本结束------------------------------------------------
