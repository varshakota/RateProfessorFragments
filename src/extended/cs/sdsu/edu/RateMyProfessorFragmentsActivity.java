package extended.cs.sdsu.edu;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import extended.cs.sdsu.edu.ProfessorDetailsFragment.CommentListListener;
import extended.cs.sdsu.edu.ProfessorDetailsFragment.RateListener;

public class RateMyProfessorFragmentsActivity extends Activity implements
		OnProfessorSelected, RateListener, CommentListListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public void selectedProfessorId(int id) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		ProfessorDetailsFragment professorDetailsFragment = (ProfessorDetailsFragment) fragmentManager
				.findFragmentByTag("PDF");
		// if (fragmentManager.findFragmentByTag("PDF") == null) {
		if (professorDetailsFragment == null) {
			professorDetailsFragment = new ProfessorDetailsFragment();
			fragmentTransaction.replace(R.id.detailFrameLayout,
					professorDetailsFragment, "PDF");
		} else {
			// fragmentTransaction.show(pdf);
			fragmentTransaction.replace(R.id.detailFrameLayout,
					professorDetailsFragment, "PDF");
		}
		fragmentTransaction.addToBackStack("details");
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		professorDetailsFragment.selectedProfessorId(id);

		// }
		// else {
		// Intent professorDetails = new Intent();
		// professorDetails
		// .setClassName("extended.cs.sdsu.edu.activity",
		// "extended.cs.sdsu.edu.activity.SelectedProfessorDetailsActivity");
		// professorDetails
		// .setAction("cs.assignment.intent.action.PROFESSOR_DETAILS");
		// professorDetails.putExtra("selectedProfessorID", id);
		// startActivity(professorDetails);
		// }
	}

	@Override
	public void onRateButtonClick(int id) {
		FragmentManager fm = getFragmentManager();
		Fragment rateProfessorFragment = new RateProfessorFragment(id);
		// if (fm.findFragmentByTag("RPF") == null) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.detailFrameLayout, rateProfessorFragment,
				"RPF");
		transaction.addToBackStack("RPF");
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		// } else {
		// Intent rateProfessorIntent = new Intent();
		// rateProfessorIntent.setClassName("extended.cs.sdsu.edu.activity",
		// "extended.cs.sdsu.edu.activity.RateProfessorActivity");
		// rateProfessorIntent
		// .setAction("cs.assignment.intent.action.RATE_PROFESSOR");
		// rateProfessorIntent.putExtra("selectedProfessorID", id);
		// startActivity(rateProfessorIntent);
		// }
	}

	@Override
	public void onCommentButtonClick(int id) {
		Fragment viewProfessorCommentsFragment = new ViewProfessorCommentsFragment(
				id);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.detailFrameLayout,
				viewProfessorCommentsFragment, "ViewProfessorComments");
		transaction.addToBackStack("comments");
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
	}
}