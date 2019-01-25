-------�漰��RMI�����ݿ�䶯���֣���Ҫ�ڴ˴��ύ���ݿ��ύ�ű�-----------
-------ORACLE�ű� 2014-01-23 ---------------------------
-----��ע�������ο�������ֱ��ʹ�ã�����ʱ������һ�ȶԡ�--------------


-----------------------------V1.3.6�汾��ʼ2014-01-23 add by qfxu------------------------
--1���豸�����Ϣ��(DEV_SOFTWARE_INFO)���������ֶΣ�SERIAL_NO_INFO_STATUS  INTEGER,
alter table DEV_SOFTWARE_INFO add (SERIAL_NO_INFO_STATUS  INTEGER);
alter table DEV_SOFTWARE_INFO add (TXT_INFO_STATUS  INTEGER);
comment on column DEV_SOFTWARE_INFO.SERIAL_NO_INFO_STATUS is '���ֺŻ��濪��״̬��1:������0:�رգ�������δ֪��';
comment on column DEV_SOFTWARE_INFO.TXT_INFO_STATUS is '�ֽ𻺴濪��״̬��1:������0:�رգ�������δ֪��';
-----------------------------V1.3.6�汾����2014-02-08------------------------------------



-----------------------------V1.3.7�汾��ʼ 2014-02-11 add by qfxu------------------------
--�豸�����Ϣ�����������ֶ�  ������Դ����������    2014-02-11  add by qfxu
--�豸������Ϣ������DEV_LOG_PATH��ע�⣬DEV_LOG_PATH��һ������·�������Ұ����ļ����Ƽ���׺  ������Դ����������    2014-02-11  add by qfxu
--1���豸�����Ϣ��(DEV_SOFTWARE_INFO)���������ֶΣ�
alter table DEV_SOFTWARE_INFO add (CIMSRP_FLAG  INTEGER);
alter table DEV_SOFTWARE_INFO add (CDMSRP_FLAG  INTEGER);
comment on column DEV_SOFTWARE_INFO.CIMSRP_FLAG is '�����ֺ�ƾ����ӡ����״̬��1:������0:�رգ�������δ֪��';
comment on column DEV_SOFTWARE_INFO.CDMSRP_FLAG is 'ȡ����ֺ�ƾ����ӡ����״̬��1:������0:�رգ�������δ֪��';
--2���豸������Ϣ��DEV_BASE_INFO�������ֶΣ�
alter table DEV_BASE_INFO add (DEV_LOG_PATH VARCHAR(200));
comment on column DEV_BASE_INFO.DEV_LOG_PATH is 'TCR�豸��־·�������磺C\:\\WSAP\\DATA\\yyyymmdd.J��';
-----------------------------V1.3.7�汾���� ------------------------------------------------


-----------------------------V1.4.0�汾��ʼ 2014-09-10 add by cqluo-------------------------
--1���豸������Ϣ��(DEV_BASE_INFO)�����ֶΣ�TRANS_RATE
--ע�⣺ 1����sql��������Ҫ�����ٶ�����ʱ��ִ�У�����ִ��
--    2��TRANS_RATE�ֶ��п����Ѿ����ڣ���Ľ��̿����Ѿ�������ִ��ǰ������ȷ�����ֶ��Ƿ���ڣ������ظ�ִ�б���
alter table DEV_BASE_INFO add (TRANS_RATE VARCHAR2(20));
comment on column DEV_BASE_INFO.TRANS_RATE is '�ļ��ϴ���������ٶ�,��λkb/s';
-----------------------------V1.4.0�汾����-------------------------------------------------

-----------------------------V1.4.1�汾��ʼ 2015-12-29 add by zhangdd------------------------
--��ҵ�����Զ���������
--1��Զ�̿��Ƽ�¼��(REMOTE_TRACE)�����ֶΣ�CACHE_FALG��COUNT_ALL_FLAG��CASHUNIT_COUNT��CASHUNIT_LIST
alter table REMOTE_TRACE add (CHECK_TYPE VARCHAR2(2));
comment on column REMOTE_TRACE.CHECK_TYPE is '���鷽ʽ��0-����ִ�У�Ĭ�ϣ���1-��ʱִ��';
alter table REMOTE_TRACE add (CACHE_FALG VARCHAR2(2));
comment on column REMOTE_TRACE.CACHE_FALG is '�Ƿ���ҪC�˽��л��洦��';
alter table REMOTE_TRACE add (COUNT_ALL_FLAG VARCHAR2(2));
comment on column REMOTE_TRACE.COUNT_ALL_FLAG is '�Ƿ񾫲����г���';
alter table REMOTE_TRACE add (CASHUNIT_COUNT INTEGER);
comment on column REMOTE_TRACE.CASHUNIT_COUNT is '��Ҫ����ĳ�����Ŀ';
alter table REMOTE_TRACE add (CASHUNIT_LIST VARCHAR2(100));
comment on column REMOTE_TRACE.CASHUNIT_LIST is '�߼������������б�';

--2����������(REMOTE_COUNT_RESULT_INFO)
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
'��������';

comment on column REMOTE_COUNT_RESULT_INFO.LOGIC_ID is
'���';

comment on column REMOTE_COUNT_RESULT_INFO.TRACE_LOGIC_ID is
'Զ�̿��ƽ�����';

comment on column REMOTE_COUNT_RESULT_INFO.COMM_RESULT is
'ִ�н��';

comment on column REMOTE_COUNT_RESULT_INFO.DATETIME is
'������֯ʱ��';

comment on column REMOTE_COUNT_RESULT_INFO.COMM_START_TIME is
'����ִ�п�ʼʱ��';

comment on column REMOTE_COUNT_RESULT_INFO.COMM_END_TIME is
'����ִ�н���ʱ��';

comment on column REMOTE_COUNT_RESULT_INFO.HOST_AMOUNT is
'β����';

comment on column REMOTE_COUNT_RESULT_INFO.LOCAL_AMOUNT is
'�豸Ӧ�н��';

comment on column REMOTE_COUNT_RESULT_INFO.PUID_BEFORE is
'ִ�������ʼǰ�ĳ�����ϸ-������ID����';

comment on column REMOTE_COUNT_RESULT_INFO.PU_POS_NAME_BEFORE is
'ִ�������ʼǰ�ĳ�����ϸ-����������λ��������';
 
comment on column REMOTE_COUNT_RESULT_INFO.PU_REJECT_COUNT_BEFORE is
'ִ�������ʼǰ�ĳ�����ϸ-������ܳ���������';

comment on column REMOTE_COUNT_RESULT_INFO.PU_COUNT_BEFORE is
'ִ�������ʼǰ�ĳ�����ϸ-�����䵱ǰ��������';

comment on column REMOTE_COUNT_RESULT_INFO.TYPE_BEFORE is
'ִ�������ʼǰ�ĳ�����ϸ-�������ͼ���';

comment on column REMOTE_COUNT_RESULT_INFO.NOTE_VALUE_BEFORE is
'ִ�������ʼǰ�ĳ�����ϸ-��ֵ����';

 comment on column REMOTE_COUNT_RESULT_INFO.STATUS_BEFORE is
'ִ�������ʼǰ�ĳ�����ϸ-����״̬����';  

comment on column REMOTE_COUNT_RESULT_INFO.PUID_AFTER is
'ִ�������ɺ�ĳ�����ϸ-������ID����';

comment on column REMOTE_COUNT_RESULT_INFO.PU_POS_NAME_AFTER is
'ִ�������ɺ�ĳ�����ϸ-����������λ��������';
 
comment on column REMOTE_COUNT_RESULT_INFO.PU_REJECT_COUNT_AFTER is
'ִ�������ɺ�ĳ�����ϸ-������ܳ���������';

comment on column REMOTE_COUNT_RESULT_INFO.PU_COUNT_AFTER is
'ִ�������ɺ�ĳ�����ϸ-�����䵱ǰ��������';

comment on column REMOTE_COUNT_RESULT_INFO.TYPE_AFTER is
'ִ�������ɺ�ĳ�����ϸ-�������ͼ���';

comment on column REMOTE_COUNT_RESULT_INFO.NOTE_VALUE_AFTER is
'ִ�������ɺ�ĳ�����ϸ-��ֵ����';

 comment on column REMOTE_COUNT_RESULT_INFO.STATUS_AFTER is
'ִ�������ɺ�ĳ�����ϸ-����״̬����'; 
-----------------------------V1.4.1�汾����------------------------------------------------
