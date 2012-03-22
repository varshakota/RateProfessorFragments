package extended.cs.sdsu.edu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import extended.cs.sdsu.edu.domain.Professor;
import extended.cs.sdsu.edu.service.ApplicationFactory;
import extended.cs.sdsu.edu.service.ProfessorService;

public class ProfessorDetailsFragment extends Fragment {

	private TextView firstNameTextView;
	private TextView lastNameTextView;
	private TextView officeTextView;
	private TextView phoneTextView;
	private TextView emailTextView;
	private RatingBar averageTextView;
	private TextView totalRatingTextView;
	private Button viewCommentsButton;
	private Button rateprofessorButton;
	private int professorId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = initializeView(inflater, container);

		return view;
	}

	private View initializeView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.selected_professor_details,
				container, false);
		firstNameTextView = (TextView) view.findViewById(R.id.firstName);
		lastNameTextView = (TextView) view.findViewById(R.id.lastName);
		officeTextView = (TextView) view.findViewById(R.id.office);
		phoneTextView = (TextView) view.findViewById(R.id.phone);
		emailTextView = (TextView) view.findViewById(R.id.email);
		averageTextView = (RatingBar) view.findViewById(R.id.average);
		totalRatingTextView = (TextView) view.findViewById(R.id.totalRating);
		viewCommentsButton = (Button) view.findViewById(R.id.comment);
		rateprofessorButton = (Button) view.findViewById(R.id.rate);
		rateprofessorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rateProfessor(v);
			}
		});

		viewCommentsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				viewProfessorComments(v);

			}
		});

		return view;
	}

	public void rateProfessor(View view) {
		Fragment rpf = new RateProfessorFragment(professorId);
		// if (rpf != null & rpf.isInLayout()) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.detailFrameLayout, rpf, "RPF");
		transaction.addToBackStack("details");
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		// }
		// else {
		// Intent rateProfessorIntent = new Intent();
		// rateProfessorIntent.setClassName("extended.cs.sdsu.edu.activity",
		// "extended.cs.sdsu.edu.activity.RateProfessorActivity");
		// rateProfessorIntent
		// .setAction("cs.assignment.intent.action.RATE_PROFESSOR");
		// rateProfessorIntent.putExtra("selectedProfessorID", professorId);
		// startActivity(rateProfessorIntent);
		// }

	}

	public void viewProfessorComments(View view) {
		Fragment viewProfessorCommentsFragment = new ViewProfessorCommentsFragment(
				professorId);
		// if (viewProfessorCommentsFragment != null
		// && viewProfessorCommentsFragment.isInLayout()) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.detailFrameLayout,
				viewProfessorCommentsFragment, "ViewProfessorComments");
		transaction.addToBackStack("comments");
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		// }
		// else {
		// Intent professorCommentsIntent = new Intent();
		// professorCommentsIntent
		// .setClassName("extended.cs.sdsu.edu.activity",
		// "extended.cs.sdsu.edu.activity.ViewProfessorCommentsActivity");
		// professorCommentsIntent
		// .setAction("cs.assignment.intent.action.PROFESSOR_COMMENTS");
		// professorCommentsIntent
		// .putExtra("selectedProfessorID", professorId);
		// startActivity(professorCommentsIntent);
		// }
	}

	@Override
	public void onResume() {
		super.onResume();

		displayProfessorDetails(professorId);
	}

	private void displayProfessorDetails(int professorId) {
		Professor professorDetails = new Professor();
		ProfessorService professorDetailsService = ApplicationFactory
				.getProfessorService(getActivity());
		try {
			professorDetails = professorDetailsService
					.getProfessorDetails(professorId);
			if (firstNameTextView != null) {
				firstNameTextView.setText(professorDetails.getFirstName());
				lastNameTextView.setText(professorDetails.getLastName());
				officeTextView.setText(professorDetails.getOffice());
				phoneTextView.setText(professorDetails.getPhone());
				emailTextView.setText(professorDetails.getEmail());
				averageTextView.setRating(professorDetails.getAverage());
				totalRatingTextView.setText(String.valueOf(professorDetails
						.getTotalRatings()));
			}
		} catch (Exception e) {
			Log.e("RateMyProfessorTablet", e.getMessage(), e);
		}

	}

	public void selectedProfessorId(int professorId) {
		if (this.professorId != professorId) {
			displayProfessorDetails(professorId);
		}
		this.professorId = professorId;
	}

}
