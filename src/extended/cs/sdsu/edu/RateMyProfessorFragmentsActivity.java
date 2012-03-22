package extended.cs.sdsu.edu;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class RateMyProfessorFragmentsActivity extends Activity implements
		OnProfessorSelected {

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
		ProfessorDetailsFragment pdf = (ProfessorDetailsFragment) fragmentManager
				.findFragmentByTag("PDF");
		if (pdf == null) {
			pdf = new ProfessorDetailsFragment();
			fragmentTransaction.replace(R.id.detailFrameLayout, pdf, "PDF");
		} else {
			// fragmentTransaction.show(pdf);
			fragmentTransaction.replace(R.id.detailFrameLayout, pdf, "PDF");
		}
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		pdf.selectedProfessorId(id);
	}

}