package extended.cs.sdsu.edu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import extended.cs.sdsu.edu.domain.Professor;
import extended.cs.sdsu.edu.service.ApplicationFactory;
import extended.cs.sdsu.edu.service.ProfessorChangedListener;
import extended.cs.sdsu.edu.service.ProfessorService;

public class ProfessorListFragment extends ListFragment {

	private List<Professor> professorList = new ArrayList<Professor>();
	private ProfessorListAdapter professorListAdapter = new ProfessorListAdapter(
			professorList, getActivity());
	private ProfessorChangedListener professorChangeListener;
	private ProfessorService professorService;
	private OnProfessorSelected onProfessorSelectedListener;
	private boolean isLandScape;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		professorListAdapter = new ProfessorListAdapter(professorList,
				getActivity());
		setListAdapter(professorListAdapter);
		professorService = ApplicationFactory
				.getProfessorService(getActivity());

		View frameLayout = getActivity().findViewById(R.id.detailFrameLayout);
		isLandScape = frameLayout != null
				&& frameLayout.getVisibility() == View.VISIBLE;

		if (isLandScape) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
		initListener();
	}

	private void initListener() {
		professorChangeListener = new ProfessorChangedListener() {
			@Override
			public void professorListUpdated(List<Professor> newProfessorList) {
				professorListAdapter.refreshList(newProfessorList);
				professorListAdapter.notifyDataSetChanged();
			}
		};
	}

	@Override
	public void onStop() {
		super.onStop();
		professorService
				.removeProfessorChangedListener(professorChangeListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		professorService.addProfessorChangedListener(professorChangeListener);

		refreshProfessorList();
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {

		Professor professor = (Professor) listView.getItemAtPosition(position);
		int selectedProfessorId = professor.getId();

		if (isLandScape) {
			getListView().setItemChecked(position, true);
			onProfessorSelectedListener
					.selectedProfessorId(selectedProfessorId);
		}
		// else {
		// Intent professorDetails = new Intent();
		// professorDetails
		// .setClassName("extended.cs.sdsu.edu.activity",
		// "extended.cs.sdsu.edu.activity.SelectedProfessorDetailsActivity");
		// professorDetails
		// .setAction("cs.assignment.intent.action.PROFESSOR_DETAILS");
		// professorDetails.putExtra("selectedProfessorID",
		// selectedProfessorId);
		// startActivity(professorDetails);
		// }
	}

	private void refreshProfessorList() {
		try {
			professorList = ApplicationFactory.getProfessorService(
					getActivity()).getProfessorList();
			professorListAdapter.refreshList(professorList);
			professorListAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e("RateMyProfessorTablet", e.getMessage(), e);
		}
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onProfessorSelectedListener = (OnProfessorSelected) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
}
