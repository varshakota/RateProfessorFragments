package extended.cs.sdsu.edu.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import extended.cs.sdsu.edu.domain.Comment;
import extended.cs.sdsu.edu.domain.Professor;

public class DatabaseAccessor {

	private SQLiteDatabase db;

	public DatabaseAccessor(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	public boolean isProfessorTableEmpty() {
		Cursor result = db.rawQuery("SELECT * from PROFESSOR", null);
		int rowCount = result.getCount();
		result.close();
		return rowCount == 0;
	}

	public void createProfessors(List<Professor> professorListData) {
		for (int i = 0; i < professorListData.size(); i++) {
			Professor professorFromList = professorListData.get(i);
			ContentValues cv = new ContentValues();
			cv.put("ID", professorFromList.getId());
			cv.put("firstname", professorFromList.getFirstName());
			cv.put("lastname", professorFromList.getLastName());
			db.insert("PROFESSOR", null, cv);
		}
	}

	public List<Professor> retrieveProfessors() {
		List<Professor> professorList = new ArrayList<Professor>();
		Cursor result = db.rawQuery("SELECT * FROM PROFESSOR", null);
		result.moveToFirst();
		Professor professor = null;
		while (result.isAfterLast() == false) {
			professor = new Professor();
			professor.setId(result.getInt(result.getColumnIndex("ID")));
			professor.setLastName(result.getString(result
					.getColumnIndex("lastname")));
			professor.setFirstName(result.getString(result
					.getColumnIndex("firstname")));
			professorList.add(professor);
			result.moveToNext();
		}
		result.close();
		return professorList;
	}

	public boolean isProfessorDetailsEmpty(int selectedProfessorId) {
		Cursor result = db.rawQuery(
				"SELECT * from PROFESSOR_DETAILS WHERE ID=?",
				new String[] { String.valueOf(selectedProfessorId) });
		int rowCount = result.getCount();
		result.close();
		return rowCount == 0;
	}

	public void addProfessorDetails(Professor professorDetails) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", professorDetails.getId());
		contentValues.put("office", professorDetails.getOffice());
		contentValues.put("phone", professorDetails.getPhone());
		contentValues.put("email", professorDetails.getEmail());
		contentValues.put("average", professorDetails.getAverage());
		contentValues.put("totalrating", professorDetails.getTotalRatings());

		db.insert("PROFESSOR_DETAILS", null, contentValues);
	}

	public Professor retrieveProfessorDetails(int selectedProfessorId) {
		Professor professorDetails = new Professor();
		Cursor result = db
				.rawQuery(
						"SELECT * FROM PROFESSOR,PROFESSOR_DETAILS WHERE PROFESSOR.ID=PROFESSOR_DETAILS.ID AND PROFESSOR.ID = ?",
						new String[] { String.valueOf(selectedProfessorId) });
		result.moveToFirst();
		professorDetails.setId(selectedProfessorId);
		professorDetails.setFirstName(result.getString(result
				.getColumnIndex("firstname")));
		professorDetails.setLastName(result.getString(result
				.getColumnIndex("lastname")));
		professorDetails.setOffice(result.getString(result
				.getColumnIndex("office")));
		professorDetails.setPhone(result.getString(result
				.getColumnIndex("phone")));
		professorDetails.setEmail(result.getString(result
				.getColumnIndex("email")));
		professorDetails.setAverage(result.getFloat(result
				.getColumnIndex("average")));
		professorDetails.setTotalRatings(result.getInt(result
				.getColumnIndex("totalrating")));
		result.close();
		return professorDetails;
	}

	public boolean isProfessorCommentsEmpty(int selectedProfessorId) {
		Cursor result = db.rawQuery("SELECT * from COMMENTS WHERE ID=?",
				new String[] { String.valueOf(selectedProfessorId) });
		int rowCount = result.getCount();
		result.close();
		return rowCount == 0;
	}

	public void saveComments(List<Comment> comments) {
		ContentValues contentValues = new ContentValues();
		for (int i = 0; i < comments.size(); i++) {
			Comment comment = comments.get(i);
			contentValues.put("ID", comment.getProfessorId());
			contentValues.put("commentsId", comment.getCommentsId());
			contentValues.put("commentsTxt", comment.getText());
			contentValues.put("date", comment.getDate());
			db.insert("COMMENTS", null, contentValues);
		}
	}

	public List<Comment> retrieveComments(int selectedProfessorId) {
		Cursor result = db.rawQuery(
				"SELECT * FROM COMMENTS WHERE ID=? ORDER BY commentsId DESC",
				new String[] { String.valueOf(selectedProfessorId) });
		List<Comment> professorComments = new ArrayList<Comment>();
		result.moveToFirst();
		Comment comment = null;
		while (result.isAfterLast() == false) {
			comment = new Comment();
			comment.setProfessorId(selectedProfessorId);
			comment.setCommentsId(result.getInt(result
					.getColumnIndex("commentsId")));
			comment.setText(result.getString(result
					.getColumnIndex("commentsTxt")));
			comment.setDate(result.getString(result.getColumnIndex("date")));
			professorComments.add(comment);
			result.moveToNext();
		}
		result.close();
		return professorComments;
	}

	public int getLatestCommentsId(int professorId) {

		Cursor result = db
				.rawQuery(
						"SELECT commentsId FROM COMMENTS WHERE ID=? ORDER BY commentsId DESC",
						new String[] { String.valueOf(professorId) });
		int commentsId = 0;
		if (result.getCount() > 0) {
			result.moveToFirst();
			commentsId = result.getInt(result.getColumnIndex("commentsId"));
		}
		result.close();
		return commentsId;
	}

	public void updateProfessorDetails(Professor professor) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("office", professor.getOffice());
		contentValues.put("phone", professor.getPhone());
		contentValues.put("email", professor.getEmail());
		contentValues.put("average", professor.getAverage());
		contentValues.put("totalrating", professor.getTotalRatings());
		db.update("PROFESSOR_DETAILS", contentValues, "ID=?",
				new String[] { String.valueOf(professor.getId()) });
	}

	public void updateProfessorName(Professor professor) {
		ContentValues contentValuesName = new ContentValues();
		contentValuesName.put("firstname", professor.getFirstName());
		contentValuesName.put("lastname", professor.getLastName());
		db.update("PROFESSOR", contentValuesName, "ID=?",
				new String[] { String.valueOf(professor.getId()) });
	}

	public boolean isProfessorEmpty(int professorId) {
		Cursor name = db.rawQuery("SELECT * FROM PROFESSOR WHERE ID=?",
				new String[] { String.valueOf(professorId) });
		int count = name.getCount();
		name.close();
		return count == 0;
	}

	public void insertProfessor(Professor professor) {
		ContentValues contentValuesName = new ContentValues();
		contentValuesName.put("firstname", professor.getFirstName());
		contentValuesName.put("lastname", professor.getLastName());
		contentValuesName.put("ID", professor.getId());
		db.insert("PROFESSOR", null, contentValuesName);
	}
}
