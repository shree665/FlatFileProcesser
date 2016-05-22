delete from CCT001.batch_lnch_params where BATCH_JOB_LNCH_ID = 1250;
delete from CCT001.batch_job_lnch where BATCH_JOB_LNCH_ID = 1250;

--Delete old TXN records
delete from CCT001.job_ctrl_txn where job_ctrl_id in ( select job_ctrl_id from CCT001.job_ctrl where JOB_NAM = 'SUBEDIFILE_J01');
--Delete old CTRL records
delete from CCT001.job_ctrl where JOB_NAM = 'SUBEDIFILE_J01';

--------------------------------------------------------------------------------
insert into CCT001.batch_job_lnch (
	BATCH_JOB_LNCH_ID, JOB_LNCH_NAME, JOB_CTX_FILE_LOC, JOB_NAME, 
	VERSION, NEW_INSTANCE_FLG, ACTIVE_FLG, DAILY_FLG, JOB_TYPE
) values (
	1250, 'SUBEDIFILE_J01', 'SUBEDIFILE_J01-context.xml', 'SUBEDIFILE_J01',
	0, 'Y', 'Y', 'N', 'CCM'
);

---------------------------------------------------------------------------------
insert into CCT001.batch_lnch_params (BATCH_JOB_LNCH_ID, JOB_PARAM_ID, JOB_PARAM_NAME, JOB_PARAM_VALUE) values (
	1250, 1, 'option.job.launcher', 'yes');
insert into CCT001.batch_lnch_params (BATCH_JOB_LNCH_ID, JOB_PARAM_ID, JOB_PARAM_NAME, JOB_PARAM_VALUE) values (
	1250, 2, 'job.launcher.sbparam', 'RN.CHUNK.SIZE=500');
insert into CCT001.batch_lnch_params (BATCH_JOB_LNCH_ID, JOB_PARAM_ID, JOB_PARAM_NAME, JOB_PARAM_VALUE) values (
	1250, 3, 'job.launcher.sbparam', 'COMMIT.INTERVAL=100');
insert into CCT001.batch_lnch_params (BATCH_JOB_LNCH_ID, JOB_PARAM_ID, JOB_PARAM_NAME, JOB_PARAM_VALUE) values (
	1250, 4, 'job.launcher.sbparam', 'WEEKLY.GO.CODE=7');
insert into CCT001.batch_lnch_params (BATCH_JOB_LNCH_ID, JOB_PARAM_ID, JOB_PARAM_NAME, JOB_PARAM_VALUE) values (
	1250, 5, 'job.launcher.sbparam', 'MONTHLY.GO.CODE=18');
--------------------------------------------------------------------------------

--Merges
insert into CCT001.job_ctrl (JOB_CTRL_ID,JOB_NAM,DB_CD,TBL_NAM,FREQ_CD,BEHAV_CD,ID_COL_NAM,UPDT_COL_NAM,CRTD_COL_NAM)
	VALUES (NEXT VALUE FOR CCT001.JOB_CTRL_SEQ,'SUBEDIFILE_J01','ICM','AGENT','D','M','AGNT_ID',NULL,NULL);










	
	
	