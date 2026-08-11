package controller;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.JobBean;
import service.implementation.JobServiceImpl;

public class JobController {
	private final JobServiceImpl jobSvc;
	
	public JobController() {
		 jobSvc = new JobServiceImpl();
	}
	
	public boolean createJob(HttpSession session, JobBean job) {
		return jobSvc.createJob(session, job);
	}
	
	public boolean deleteJob(HttpSession session, int jobId) {
		return jobSvc.deleteJob(session, jobId);
	}
	
	public boolean updateJob(HttpSession session, JobBean job) {
		return jobSvc.updateJob(session, job);
	}
	
	public boolean hasJob(String name) {
		return jobSvc.hasJob(name);
	}
	
	public JobBean getJob(int jobId) {
		return jobSvc.getJob(jobId);
	}
	
	public Vector<JobBean> getJobs(String jobName) {
		return jobSvc.getJobs(jobName);
	}
}
