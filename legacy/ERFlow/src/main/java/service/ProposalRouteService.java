package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.view.ViewProposalRouteBean;

public interface ProposalRouteService extends Service {
	boolean createProposalRoute(HttpServletRequest request);

	boolean createProposalRoute(ViewProposalRouteBean bean);

	boolean deleteProposalRoute(int proposalRouteId);

	boolean updateProposalRoute(HttpServletRequest request);

	boolean updateProposalRoute(ViewProposalRouteBean bean);

	int getProposalRoutesCount(String keyfield, String keyword, String id);

	ViewProposalRouteBean getProposalRouteView(int proposalRouteId);

	Vector<ViewProposalRouteBean> getProposalRoutes(String keyfield, String keyword, int start, int count, String id);
}
