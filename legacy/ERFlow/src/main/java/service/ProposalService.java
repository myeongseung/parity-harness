package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.ProposalBean;
import model.view.ViewProposalBean;

public interface ProposalService extends Service {	
	boolean createProposal(HttpServletRequest request);

	boolean createProposal(ProposalBean bean);
	
	boolean confirmProposal(ProposalBean bean);
	
	boolean confirmProposals(ProposalBean bean);
	
	boolean isFinalStep(ProposalBean bean);
	
	boolean rejectProposal(ProposalBean bean);
	
	boolean rejectProposals(ProposalBean bean);
	
	boolean hasProposal(long documentId);

	ProposalBean getProposal(long proposalId);

	ViewProposalBean getProposalView(long proposalId);
	
	Vector<ViewProposalBean> getProposalViews();

	Vector<ViewProposalBean> getProposalViews(long documentId);

	Vector<ViewProposalBean> getProposalViews(String userId, int result, int start, int cnt );
	
	int getProposalViewsCount(String userId, int result);
	
	Vector<ViewProposalBean> getRecentProposalViews();
	
	Vector<ViewProposalBean> getRecentProposalViews(String userId, int start, int cnt);
	
}
