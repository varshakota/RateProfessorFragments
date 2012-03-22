package extended.cs.sdsu.edu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import extended.cs.sdsu.edu.domain.Professor;
import extended.cs.sdsu.edu.service.ApplicationFactory;
import extended.cs.sdsu.edu.service.ProfessorCommentsService;
import extended.cs.sdsu.edu.service.ProfessorRatingService;

public class RateProfessorFragment extends Fragment {

	private RatingBar ratingBar;
	private TextView commentsText;
	private ProfessorRatingService ratingService;
	private ProfessorCommentsService commentsService;
	private TextView averageTextView;
	private TextView totalRatingTextView;
	private AlertDialog.Builder builder;
	private View layout;
	private AlertDialog alertDialog;
	private int selectedProfessorId;
	private Button submitRating;

	public RateProfessorFragment() {

	}

	public RateProfessorFragment(int professorId) {
		super();
		Bundle bundle = new Bundle();
		bundle.putInt("professorId", professorId);
		this.setArguments(bundle);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectedProfessorId = getArguments() != null ? getArguments().getInt(
				"professorId") : 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rateView = inflater.inflate(R.layout.rate_professor, container,
				false);
		ratingBar = (RatingBar) rateView.findViewById(R.id.ratingBar);
		commentsText = (EditText) rateView.findViewById(R.id.commentsText);
		ratingService = ApplicationFactory
				.getProfessorRatingService(getActivity());
		commentsService = ApplicationFactory
				.getProfessorCommentsService(getActivity());
		builder = new AlertDialog.Builder(getActivity());
		inflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.success_page, null);
		averageTextView = (TextView) layout.findViewById(R.id.averageSuccess);
		totalRatingTextView = (TextView) layout
				.findViewById(R.id.totalRatingsSuccess);
		submitRating = (Button) rateView.findViewById(R.id.submit);
		submitRating.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submitRatingComments(v);
			}
		});

		return rateView;
	}

	private void submitRatingComments(View click) {
		try {
			int iRating = (int) ratingBar.getRating();
			String rating = new Integer(iRating).toString();
			String comments = commentsText.getText().toString();
			if (rating.equals("0") && (comments.equals(""))) {
				Toast.makeText(getActivity(), "Enter rating and comments",
						Toast.LENGTH_SHORT).show();
			} else if (rating.equals("0") && (!comments.equals(""))) {
				Toast.makeText(getActivity(), "Enter a rating",
						Toast.LENGTH_SHORT).show();
			} else {
				int professorCommentsStatusCode = commentsService
						.submitProfessorComments(selectedProfessorId, comments);
				int professorRatingStatusCode = ratingService
						.submitProfessorRating(selectedProfessorId, rating);

				onSuccessDisplayDialog(professorCommentsStatusCode,
						professorRatingStatusCode, selectedProfessorId, rating);
			}
		} catch (Exception e) {
			Toast.makeText(getActivity(),
					"Sorry for the inconvenience. Try again later.",
					Toast.LENGTH_SHORT).show();
			Log.e("RateMyProfessorTablet", e.getMessage(), e);
		}
	}

	private void onSuccessDisplayDialog(int professorCommentsStatusCode,
			int professorRatingStatusCode, int selectedProfessorId,
			String rating) throws Exception {
		if (professorCommentsStatusCode == 200
				&& professorRatingStatusCode == 200) {
			Professor professorWithNewRatings;
			professorWithNewRatings = ratingService.getProfessorRating(
					selectedProfessorId, rating);
			Float average = professorWithNewRatings.getAverage();
			Integer totalrating = new Integer(
					professorWithNewRatings.getTotalRatings());
			averageTextView.setText(average.toString());
			totalRatingTextView.setText(totalrating.toString());
			builder.setView(layout);
			builder.setTitle("Comments and Rating submitted successfully!!");
			builder.setCancelable(false);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							try {
								ApplicationFactory.getProfessorService(
										getActivity())
										.getUpdatedProfessorListFromServer();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			alertDialog = builder.create();
			alertDialog.show();
		} else {
			Toast.makeText(getActivity(),
					"Sorry unable to connect to the network. Try again later.",
					Toast.LENGTH_SHORT).show();
		}

	}
}