package extended.cs.sdsu.edu.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import extended.cs.sdsu.edu.database.DatabaseAccessor;
import extended.cs.sdsu.edu.domain.JSONObjectMapper;
import extended.cs.sdsu.edu.domain.Professor;
import extended.cs.sdsu.edu.domain.SharedPreferenceWrapper;
import extended.cs.sdsu.edu.network.GETNetworkConnection;
import extended.cs.sdsu.edu.util.ApplicationUtils;

public class ProfessorService {

	private JSONObjectMapper jsonObjectMapper;
	private DatabaseAccessor db;
	private SharedPreferenceWrapper sharedPreferenceWrapper;
	private List<ProfessorChangedListener> professorChangedListenerList = new ArrayList<ProfessorChangedListener>();

	public ProfessorService(Context context) {
		jsonObjectMapper = new JSONObjectMapper();
		db = ApplicationFactory.getDatabaseAccessor(context);
		sharedPreferenceWrapper = new SharedPreferenceWrapper(context);
	}

	public void addProfessorChangedListener(ProfessorChangedListener listener) {
		professorChangedListenerList.add(listener);
	}

	public void removeProfessorChangedListener(ProfessorChangedListener listener) {
		professorChangedListenerList.remove(listener);
	}

	public void notifyProfessorsChanged(List<Professor> professors) {
		for (ProfessorChangedListener listener : professorChangedListenerList) {
			listener.professorListUpdated(professors);
		}
	}

	public List<Professor> getProfessorList() throws Exception {
		List<Professor> professorListData = new ArrayList<Professor>();
		String dateAccessed = "";
		GETNetworkConnection networkConnection = new GETNetworkConnection();

		if (db.isProfessorTableEmpty()) {
			String url = "http://bismarck.sdsu.edu/rateme/list";
			dateAccessed = ApplicationUtils.getCurrentDateString();
			sharedPreferenceWrapper.putString("dateAccessed", dateAccessed);
			String responseBody = networkConnection.execute(url).get();
			if (responseBody != null) {
				JSONArray jsonProfessorArray;
				jsonProfessorArray = new JSONArray(responseBody);
				professorListData = jsonObjectMapper
						.convertJsonProfessorArrayToList(jsonProfessorArray);
				db.createProfessors(professorListData);
			}
		} else {
			professorListData = db.retrieveProfessors();
			getUpdatedProfessorListFromServer();
		}
		return professorListData;
	}

	public void getUpdatedProfessorListFromServer() throws Exception {

		AsyncTask<Void, Void, List<Professor>> task = new AsyncTask<Void, Void, List<Professor>>() {

			@Override
			protected void onPostExecute(List<Professor> result) {
				super.onPostExecute(result);
				notifyProfessorsChanged(result);
			}

			@Override
			protected List<Professor> doInBackground(Void... params) {
				String dateAccessed = sharedPreferenceWrapper
						.getString("dateAccessed");
				String url = "http://bismarck.sdsu.edu/rateme/list/since/"
						+ dateAccessed;
				String responseBody = null;
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				try {
					responseBody = httpClient.execute(httpget, responseHandler);
					if (responseBody != null) {
						JSONArray jsonProfessorArray;
						jsonProfessorArray = new JSONArray(responseBody);
						List<Professor> newProfessorListData = jsonObjectMapper
								.convertJsonProfessorArrayToList(jsonProfessorArray);
						updateProfessor(newProfessorListData);

						return db.retrieveProfessors();
					}
				} catch (Exception e) {
					Log.e("RateMyProfessorTablet", e.getMessage(), e);
				}
				httpClient.getConnectionManager().shutdown();

				return null;
			}
		};
		task.execute();
	}

	public Professor getProfessorDetails(int selectedProfessorId)
			throws Exception {
		Professor professorDetails = new Professor();
		if (db.isProfessorDetailsEmpty(selectedProfessorId)) {
			String url = "http://bismarck.sdsu.edu/rateme/instructor/"
					+ selectedProfessorId;
			GETNetworkConnection networkConnection = new GETNetworkConnection();
			String responseBody = networkConnection.execute(url).get();
			JSONObject jsonProfessorDetails = new JSONObject(responseBody);
			professorDetails = jsonObjectMapper
					.covertJsonObjectToProfessor(jsonProfessorDetails);
			professorDetails.setId(selectedProfessorId);
			db.addProfessorDetails(professorDetails);
		} else {
			professorDetails = db.retrieveProfessorDetails(selectedProfessorId);
			getUpdatedProfessorListFromServer();
		}
		return professorDetails;
	}

	public void updateProfessor(List<Professor> newProfessorListData) {
		int professorId;
		for (int i = 0; i < newProfessorListData.size(); i++) {
			Professor professor = newProfessorListData.get(i);
			professorId = professor.getId();
			if (db.isProfessorEmpty(professorId)) {
				db.insertProfessor(professor);
			} else {
				db.updateProfessorName(professor);
			}
			if (!(db.isProfessorDetailsEmpty(professorId))) {
				String url = "http://bismarck.sdsu.edu/rateme/instructor/"
						+ professorId;
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = null;
				JSONObjectMapper jsonObjectMapper = new JSONObjectMapper();
				try {
					responseBody = httpClient.execute(httpget, responseHandler);
					JSONObject jsonProfessorDetails = new JSONObject(
							responseBody);
					professor = jsonObjectMapper
							.covertJsonObjectToProfessor(jsonProfessorDetails);
					db.updateProfessorDetails(professor);
					db.updateProfessorName(professor);
				} catch (Exception e) {
					Log.e("RateMyProfessorTablet", e.getMessage(), e);
				}
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

}
