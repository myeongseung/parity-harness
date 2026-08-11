package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.ProposalBean;
import model.view.ViewProposalBean;
import service.implementation.ProposalServiceImpl;

public class ProposalController {
	private final ProposalServiceImpl proposalSvc;
	
	public ProposalController() {
		proposalSvc = new ProposalServiceImpl();
	}
	
	public boolean createProposal(HttpServletRequest request) {
		return proposalSvc.createProposal(request);
	}
	
	public boolean createProposal(ProposalBean proposal) {
		return proposalSvc.createProposal(proposal);
	}
	
	public boolean confirmProposal(ProposalBean bean) {
		return proposalSvc.confirmProposal(bean);
	}
	
	public boolean confirmProposals(ProposalBean bean) {
		return proposalSvc.confirmProposals(bean);
	}
	
	public boolean isFinalStep(ProposalBean bean) {
		return proposalSvc.isFinalStep(bean);
	}
	
	public boolean rejectProposal(ProposalBean bean) {
		return proposalSvc.rejectProposal(bean);
	}
	
	public boolean rejectProposals(ProposalBean bean) {
		return proposalSvc.rejectProposals(bean);
	}
	
	public boolean hasProposal(long documentId) {
		return proposalSvc.hasProposal(documentId);
	}
	
	public ProposalBean getProposal(long proposalId) {
		return proposalSvc.getProposal(proposalId);
	}
	
	public ViewProposalBean getProposalView(long proposalId) {
		return proposalSvc.getProposalView(proposalId);
	}
	
	public Vector<ViewProposalBean> getProposalViews() {
		return proposalSvc.getProposalViews();
	}
	
	public Vector<ViewProposalBean> getProposalViews(long documentId) {
		return proposalSvc.getProposalViews(documentId);
	}
	
	public Vector<ViewProposalBean> getProposalViews(String userId, int result, int start, int cnt) {
		return proposalSvc.getProposalViews(userId, result, start, cnt);
	}
	
	public int getProposalViewsCount(String userId, int result) {
		return proposalSvc.getProposalViewsCount(userId, result);
	}
	
	public Vector<ViewProposalBean> getRecentProposalViews() {
		return proposalSvc.getRecentProposalViews();
	}
	
	public Vector<ViewProposalBean> getRecentProposalViews(String userId, int start, int cnt) {
		return proposalSvc.getRecentProposalViews(userId, start, cnt);
	}
}
