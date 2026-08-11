package service;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.JobBean;

public interface JobService extends Service {
	boolean createJob(HttpSession session, JobBean job);

	boolean deleteJob(HttpSession session, int jobId);

	boolean updateJob(HttpSession session, JobBean job);
	
	boolean hasJob(String name);

	JobBean getJob(int jobId);

	// 불러올 때 관리자를 제외하고 불러올 것 (관리자 직급 번호: -1)
	Vector<JobBean> getJobs(String jobName);
}
