package extended.cs.sdsu.edu;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import extended.cs.sdsu.edu.domain.Comment;
import extended.cs.sdsu.edu.service.ApplicationFactory;
import extended.cs.sdsu.edu.service.ProfessorCommentsChangedListener;
import extended.cs.sdsu.edu.service.ProfessorCommentsService;

public class ViewProfessorCommentsFragment extends ListFragment implements
		ProfessorCommentsChangedListener {

	private int selectedProfessorId;
	private ProfessorCommentsService professorCommentsService;
	private List<Comment> professorCommentsList = new ArrayList<Comment>();
	private CommentsListAdapter commentsListAdapter;

	public ViewProfessorCommentsFragment() {

	}

	public ViewProfessorCommentsFragment(int professorId) {
		super();
		Bundle bundle = new Bundle();
		bundle.putInt("professorId", professorId);
		this.setArguments(bundle);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectedProfessorId = getArguments() != null ? getArguments().getInt(
				"professorId") : 0;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// setContentView(R.layout.professor_comments_list);
		// Bundle bundleProfessorId = getIntent().getExtras();
		//
		// selectedProfessorId =
		// bundleProfessorId.getInt("selectedProfessorID");

		professorCommentsService = ApplicationFactory
				.getProfessorCommentsService(getActivity());
		commentsListAdapter = new CommentsListAdapter(professorCommentsList,
				getActivity());
		setListAdapter(commentsListAdapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		professorCommentsService.addProfessorCommentsChangedListener(this);
		refreshProfessorComments();
	}

	@Override
	public void onPause() {
		super.onPause();
		professorCommentsService.removeProfessorCommentsChangedListener(this);
	}

	private void refreshProfessorComments() {
		try {
			professorCommentsList = professorCommentsService
					.getProfessorComments(selectedProfessorId);

			commentsListAdapter.refreshList(professorCommentsList);
			commentsListAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e("RateMyProfessorTablet", e.getMessage(), e);
		}
	}

	@Override
	public void professorCommentsUpdated(List<Comment> newProfessorComments) {
		commentsListAdapter.refreshList(newProfessorComments);
		commentsListAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View commentsView = inflater.inflate(R.layout.comments_list, container,
				false);
		return commentsView;
	}

}
