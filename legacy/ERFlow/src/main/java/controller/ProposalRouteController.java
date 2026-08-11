package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.DepartmentBean;
import model.view.ViewProposalRouteBean;
import service.implementation.ProposalRouteServiceImpl;

public class ProposalRouteController {
	private final ProposalRouteServiceImpl proposalRouteSvc;
	
	public ProposalRouteController() {
		proposalRouteSvc = new ProposalRouteServiceImpl();
	}
	
	public boolean createProposalRoute(HttpServletRequest request) {
		return proposalRouteSvc.createProposalRoute(request);
	}
	
	public boolean createProposalRoute(ViewProposalRouteBean bean) {
		return proposalRouteSvc.createProposalRoute(bean);
	}
	
	public boolean deleteProposalRoute(int proposalRouteId) {
		return proposalRouteSvc.deleteProposalRoute(proposalRouteId);
	}
	
	public boolean updateProposalRoute(HttpServletRequest request) {
		return proposalRouteSvc.updateProposalRoute(request);
	}
	
	public boolean updateProposalRoute(ViewProposalRouteBean bean) {
		return proposalRouteSvc.updateProposalRoute(bean);
	}
	
	public Vector<ViewProposalRouteBean> getProposalRouteViews(String keyfield, String keyword, int start, int count,
			String id) {
		return proposalRouteSvc.getProposalRoutes(keyfield, keyword, start, count, id);
	}
	
	public int getProposalRoutesCount(String keyfield, String keyword, String id) {
		return proposalRouteSvc.getProposalRoutesCount(keyfield, keyword, id);
	}
	
	public ViewProposalRouteBean getProposalRouteView(int proposalRouteId) {
		return proposalRouteSvc.getProposalRouteView(proposalRouteId);
	}
}
