package com.svs.learn.bjms.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import com.svs.learn.bjms.bean.JobDetails;
import com.svs.learn.bjms.bean.JobInstance;
import com.svs.learn.bjms.enums.JobStatus;

@Repository
public class JobManageDao {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final String SQL_INSERT_JOB = "insert into jms_job (name, job_desc, jar_path, schedule_opt, schedule_data, priority, active_flag) values(?, ?, ?, ?, ?, ?, ?)";

	private static final String SQL_INSERT_PARAMS = "insert into jms_job_params(job_id, param) values (?, ?)";

	public JobDetails insertJmsJob(JobDetails jobDetails) {

		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jobDetails.setActiveFlag(JobDetails.ACTIVE_FLAG);

		jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(SQL_INSERT_JOB, new String[] { "job_id" });
			ps.setString(1, jobDetails.getName());
			ps.setString(2, jobDetails.getDescription());
			ps.setString(3, jobDetails.getExecPath());
			ps.setString(4, jobDetails.getScheduleOpt());
			ps.setString(5, jobDetails.getScheduleData());
			ps.setInt(6, jobDetails.getPriority());
			ps.setString(7, jobDetails.getActiveFlag());
			return ps;
		}, keyHolder);

		jobDetails.setJobId(keyHolder.getKey().intValue());
		saveJobConfigParams(jobDetails);
		return jobDetails;
	}

	private void saveJobConfigParams(JobDetails jobDetails) {

		if (!jobDetails.getConfigParams().isEmpty()) {
			List<Object[]> batchParams = new ArrayList<>();
			for (String param : jobDetails.getConfigParams()) {
				batchParams.add(new Object[] { jobDetails.getJobId(), param });
			}
			jdbcTemplate.batchUpdate(SQL_INSERT_PARAMS, batchParams);
		}
	}

	public static final String SQL_UPDATE_JOB = "update jms_job set name=?, job_desc=?, jar_path=?, schedule_opt=?, schedule_data=?, priority=?, active_flag=? where job_id=?";

	public JobDetails updateJmsJob(JobDetails jobDetails) {
		jdbcTemplate.update(con -> {

			PreparedStatement ps = con.prepareStatement(SQL_UPDATE_JOB);
			ps.setString(1, jobDetails.getName());
			ps.setString(2, jobDetails.getDescription());
			ps.setString(3, jobDetails.getExecPath());
			ps.setString(4, jobDetails.getScheduleOpt());
			ps.setString(5, jobDetails.getScheduleData());
			ps.setInt(6, jobDetails.getPriority());
			ps.setString(7, jobDetails.getActiveFlag());
			ps.setInt(8, jobDetails.getJobId());
			return ps;

		});
		saveJobConfigParams(jobDetails);
		return jobDetails;
	}

	private static final String SQL_INSERT_INSTANCE = "insert into jms_job_instance(job_id, status) values (?, ?)";

	public JobInstance insertJobInstance(JobDetails jobDetails) {

		JobInstance jobInstance = new JobInstance();
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

		String instanceStatus = JobStatus.QUEUED.val();
		jdbcTemplate.update((con) -> {

			PreparedStatement ps = con.prepareStatement(SQL_INSERT_INSTANCE, new String[] { "instance_id" });
			ps.setInt(1, jobDetails.getJobId());
			ps.setString(2, instanceStatus);
			return ps;

		}, keyHolder);

		jobInstance.setJobDetails(jobDetails);
		jobInstance.setInstanceId(keyHolder.getKey().intValue());
		jobInstance.setStatus(instanceStatus);
		return jobInstance;
	}

	private static final String SQL_UPDATE_INSTANCE = "update jms_job_instance set started_on=?, ended_on=?, status=? where instance_id=?";

	public void updateJobInstance(JobInstance jobInstance) {

		jdbcTemplate.update((con) -> {

			PreparedStatement ps = con.prepareStatement(SQL_UPDATE_INSTANCE);
			ps.setTimestamp(1, getTimeStamp(jobInstance.getStartedOn()));
			ps.setTimestamp(2, getTimeStamp(jobInstance.getEndedOn()));
			ps.setString(3, jobInstance.getStatus());
			ps.setInt(4, jobInstance.getInstanceId());
			return ps;
		});
	}

	private static final String SQL_FIND_QUEUED_INSTANCE = "select i.instance_id, i.status from jms_job_instance i where i.job_id=? and i.status=? and i.started_on is null";

	public JobInstance findQueuedJobInstance(Integer jobId) {

		JobInstance jobInstance = jdbcTemplate.query((con) -> {

			PreparedStatement ps = con.prepareStatement(SQL_FIND_QUEUED_INSTANCE);
			ps.setInt(1, jobId);
			ps.setString(2, JobStatus.QUEUED.val());
			return ps;
		}, (ResultSet rs) -> {

			JobInstance instance = null;
			if (rs.next()) {
				instance = new JobInstance();
				instance.setInstanceId(rs.getInt("instance_id"));
				instance.setStatus(rs.getString("status"));
			}
			return instance;
		});

		if (jobInstance != null) {
			jobInstance.setJobDetails(getJobDetails(jobId));
		}

		return jobInstance;
	}

	public boolean anyRunningInstanceForJob(Integer jobId) {
		return !jdbcTemplate.queryForList("select 1 from jms_job_instance where job_id=? and status=?", jobId, "R")
				.isEmpty();
	}

	public void deleteAnyQueuedInstanceForJob(Integer jobId) {
		jdbcTemplate.update("delete from jms_job_instance where job_id=? and status=?", jobId, "Q");
	}

	public void deleteJobParameters(Integer jobId) {
		jdbcTemplate.update("delete from jms_job_params where job_id=?", jobId);
	}

	private static final String SQL_GET_JOB_DETAIL = "select j.job_id, j.name, j.job_desc, j.jar_path, j.schedule_opt, j.schedule_data, j.priority, j.active_flag from jms_job j where j.job_id=? ";
	private static final String SQL_GET_JOB_PARAMS = "select p.param from jms_job_params p where p.job_id=?";

	public JobDetails getJobDetails(Integer jobId) {

		JobDetails jobDetails = jdbcTemplate.query((con) -> {
			PreparedStatement ps = con.prepareStatement(SQL_GET_JOB_DETAIL);
			ps.setInt(1, jobId);
			return ps;

		}, (rs) -> {

			JobDetails details = null;
			if (rs.next()) {
				details = jobDetailsFromRs(rs);
			}
			return details;
		});

		if (jobDetails != null) {
			jobDetails.setConfigParams(jdbcTemplate.queryForList(SQL_GET_JOB_PARAMS, String.class, jobId));
		}
		return jobDetails;
	}

	private JobDetails jobDetailsFromRs(ResultSet rs) throws SQLException {

		JobDetails details = new JobDetails();
		details.setJobId(rs.getInt("job_id"));
		details.setName(rs.getString("name"));
		details.setDescription(rs.getString("job_desc"));
		details.setExecPath(rs.getString("jar_path"));
		details.setPriority(rs.getInt("priority"));
		details.setScheduleOpt(rs.getString("schedule_opt"));
		details.setScheduleData(rs.getString("schedule_data"));
		details.setActiveFlag(rs.getString("active_flag"));
		return details;
	}

	private static final String SQL_JOBS_VIEW = "select j.job_id, j.name, j.job_desc, j.jar_path, j.schedule_opt, j.schedule_data, j.priority, j.active_flag, p.param "
			+ "from jms_job j left outer join jms_job_params p on j.job_id=p.job_id order by j.job_id desc";

	private static final String SQL_RECENT_INSTANCE_VIEW = "select i.* from jms_job_instance i where i.instance_id "
			+ "in (select max(i2.instance_id) from jms_job_instance i2, jms_job j where j.job_id=i2.job_id"
			+ " group by i2.job_id)";

	public List<JobDetails> getJobDetailsAndRecentInstance() {

		HashMap<Integer, JobDetails> jobMap = new HashMap<>();

		jdbcTemplate.query((con) -> {

			PreparedStatement ps = con.prepareStatement(SQL_JOBS_VIEW);
			return ps;
		}, (RowCallbackHandler) (rs) -> {

			JobDetails details = jobMap.get(rs.getInt("job_id"));
			if (details == null) {
				details = jobDetailsFromRs(rs);
				details.setConfigParams(new ArrayList<>());
				jobMap.put(details.getJobId(), details);
			}

			details.getConfigParams().add(rs.getString("param"));
		});

		jdbcTemplate.query((con) -> {

			PreparedStatement ps = con.prepareStatement(SQL_RECENT_INSTANCE_VIEW);
			return ps;

		}, (RowCallbackHandler) (rs) -> {

			JobDetails details = jobMap.get(rs.getInt("job_id"));
			if (details != null) {

				JobInstance instance = new JobInstance();
				instance.setInstanceId(rs.getInt("instance_id"));
				instance.setStartedOn(rs.getTimestamp("started_on"));
				instance.setEndedOn(rs.getTimestamp("ended_on"));
				instance.setStatus(rs.getString("status"));
				details.setRecentInstance(instance);
			}
		});

		return jobMap.values().stream().collect(Collectors.toList());
	}

	private JobInstance instanceFromRs(ResultSet rs) throws SQLException {

		JobInstance instance = new JobInstance();
		instance.setInstanceId(rs.getInt("instance_id"));
		instance.setStartedOn(rs.getTimestamp("started_on"));
		instance.setEndedOn(rs.getTimestamp("ended_on"));
		instance.setStatus(rs.getString("status"));
		return instance;
	}

	private static final String SQL_QUERY_JOB_INSTANCES = "select * from jms_job_instance i where i.job_id=? order by i.instance_id desc";

	public List<JobInstance> getJobInstances(Integer jobId) {
		return jdbcTemplate.query((con) -> {
			PreparedStatement ps = con.prepareStatement(SQL_QUERY_JOB_INSTANCES);
			ps.setInt(1, jobId);
			return ps;
		}, (rs, i) -> {
			return instanceFromRs(rs);
		});
	}

	private Timestamp getTimeStamp(Date date) {
		return date != null ? new Timestamp(date.getTime()) : null;
	}
}
