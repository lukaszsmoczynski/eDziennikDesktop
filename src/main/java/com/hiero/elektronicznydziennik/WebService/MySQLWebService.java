package com.hiero.elektronicznydziennik.WebService;

import com.hiero.elektronicznydziennik.Enums.UserType;
import com.hiero.elektronicznydziennik.Helpers.Classes.Class;
import com.hiero.elektronicznydziennik.Helpers.Classes.RelationType;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Parent;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Student;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Teacher;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by Hiero on 08.12.2017.
 */

public class MySQLWebService {
    private static final String WEB_SERVICE_ADDRESS = "https://192.168.0.13/WebService/webService.php";
//        private static final String WEB_SERVICE_ADDRESS = "https://hiero.com.pl/inzynierka/webService.php";

    private static String currentUserToken = "";

    public JSONObject Login(String pLogin, String pHaslo) throws NoSuchAlgorithmException, JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.Login.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.Login.Parameters.LOGIN, pLogin);

        byte[] cipheredPassword = MessageDigest.getInstance("SHA-256").digest(pHaslo.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : cipheredPassword) {
            sb.append(String.format("%02X", b));
        }

        jsonParams.put(WebServiceConstants.Login.Parameters.PASSWORD, sb.toString());
        json.put(WebServiceConstants.PARAMS, jsonParams);

        JSONObject result = SendToWebService(json);
        if (result.getBoolean(WebServiceConstants.Login.Result.RESULT)) {
            currentUserToken = result.getString(WebServiceConstants.Login.Result.USER_TOKEN_RESULT);
        }
        return result;
    }

    public JSONObject Logout() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.Logout.FUNCTION_NAME);

        JSONObject result = SendToWebService(json);
        if (result.getString(WebServiceConstants.Logout.Result.RESULT).equals("true"))
            currentUserToken = "";
        return result;
    }

    public JSONObject GetChildrenOfParent(Integer pParentID) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetChildrenOfParent.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.GetChildrenOfParent.Parameters.PARENT_ID, pParentID);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetStudentNotes(Integer pStudentID) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetStudentNotes.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.GetStudentNotes.Parameters.STUDENT_ID, pStudentID);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject DeleteNote(Integer pNoteID, String pReason) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.DeleteNote.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.DeleteNote.Parameters.NOTE_ID, pNoteID);
        jsonParams.put(WebServiceConstants.DeleteNote.Parameters.REASON, pReason);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetSchedule(Integer pUserID, UserType pUserType, Date pStartDate, Integer pCount) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetSchedule.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.GetSchedule.Parameters.USER_ID, pUserID);
        jsonParams.put(WebServiceConstants.GetSchedule.Parameters.USER_TYPE, pUserType.toString());
        jsonParams.put(WebServiceConstants.GetSchedule.Parameters.BEGIN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(pStartDate));
        jsonParams.put(WebServiceConstants.GetSchedule.Parameters.COUNT, pCount);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject StartClass(Integer pClassID, String pTopic, Date pDate) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.StartClass.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.StartClass.Parameters.CLASS_ID, pClassID);
        jsonParams.put(WebServiceConstants.StartClass.Parameters.TOPIC, pTopic);
        jsonParams.put(WebServiceConstants.StartClass.Parameters.DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pDate));

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject EndClass(Integer pClassInstanceId) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.EndClass.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.EndClass.Parameters.CLASS_INSTANCE_ID, pClassInstanceId);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetStudentsOfClassInstance(Integer pClassInstanceId) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetStudentsOfClassInstance.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.GetStudentsOfClassInstance.Parameters.CLASS_INSTANCE_ID, pClassInstanceId);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject SaveAttendance(Integer classInstanceID, ArrayList<Student> studentList) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveAttendance.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveAttendance.Parameters.CLASS_INSTANCE_ID, classInstanceID);

        JSONArray studentsListArray = new JSONArray();
        for (Student student : studentList) {
            JSONObject studentObject = new JSONObject();
            studentObject.put(WebServiceConstants.SaveAttendance.Parameters.Students.ID, student.getId());
            studentObject.put(WebServiceConstants.SaveAttendance.Parameters.Students.PRESENT, student.getPresent());
            studentsListArray.put(studentObject);
        }

        jsonParams.put(WebServiceConstants.SaveAttendance.Parameters.STUDENTS, studentsListArray);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject IssueNote(Integer classInstanceID, Integer noteValue, String reason, ArrayList<Integer> studentsIDs) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.IssueNote.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.IssueNote.Parameters.CLASS_INSTANCE_ID, classInstanceID)
                .put(WebServiceConstants.IssueNote.Parameters.NOTE_VALUE, noteValue)
                .put(WebServiceConstants.IssueNote.Parameters.REASON, reason)
                .put(WebServiceConstants.IssueNote.Parameters.STUDENT_IDS, new JSONArray(studentsIDs));

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetUsers() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetUsers.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject SaveUser(int id, String login, String password, String firstName, String lastName, boolean parent,
                               boolean student, boolean teacher, boolean admin) throws NoSuchAlgorithmException {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveUser.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveUser.Parameters.ID, id == -1 ? null : id)
                .put(WebServiceConstants.SaveUser.Parameters.LOGIN, login)
                .put(WebServiceConstants.SaveUser.Parameters.NAME, firstName)
                .put(WebServiceConstants.SaveUser.Parameters.SURNAME, lastName)
                .put(WebServiceConstants.SaveUser.Parameters.PARENT, parent)
                .put(WebServiceConstants.SaveUser.Parameters.STUDENT, student)
                .put(WebServiceConstants.SaveUser.Parameters.TEACHER, teacher)
                .put(WebServiceConstants.SaveUser.Parameters.ADMIN, admin);
        if (password != null) {
            byte[] cipheredPassword = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : cipheredPassword) {
                sb.append(String.format("%02X", b));
            }
            jsonParams.put(WebServiceConstants.SaveUser.Parameters.PASSWORD, sb.toString());
        }

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetModules() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetModules.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject GetGroups() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetGroups.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject SaveGroup(int id, String name, int creationYear, Integer parentGroupId) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveGroup.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveGroup.Parameters.ID, id == -1 ? null : id)
                .put(WebServiceConstants.SaveGroup.Parameters.NAME, name)
                .put(WebServiceConstants.SaveGroup.Parameters.CREATION_YEAR, creationYear)
                .put(WebServiceConstants.SaveGroup.Parameters.PARENT_GROUP_ID, parentGroupId);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetDocumentTypes() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetDocumentTypes.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject GetFormsOfEmployment() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetFormsOfEmployment.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject GetSubjects() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetSubjects.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject GetRelationTypes() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetRelationTypes.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject SaveSubject(Integer id, String name) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveSubject.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveSubject.Parameters.ID, id == -1 ? null : id)
                .put(WebServiceConstants.SaveSubject.Parameters.NAME, name);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject SaveRelationType(Integer id, String name) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveRelationType.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveRelationType.Parameters.ID, id == -1 ? null : id)
                .put(WebServiceConstants.SaveRelationType.Parameters.NAME, name);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject SaveDocumentType(int id, String name) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveDocumentType.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveDocumentType.Parameters.ID, id == -1 ? null : id)
                .put(WebServiceConstants.SaveDocumentType.Parameters.NAME, name);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject SaveFormOfEmployment(int id, String name) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveFormOfEmployment.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveFormOfEmployment.Parameters.ID, id == -1 ? null : id)
                .put(WebServiceConstants.SaveFormOfEmployment.Parameters.NAME, name);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject SaveClass(Class pClass) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveClass.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveClass.Parameters.ID, pClass.getId())
                .put(WebServiceConstants.SaveClass.Parameters.NAME, pClass.getName())
                .put(WebServiceConstants.SaveClass.Parameters.TEACHER_ID, pClass.getTeacherId())
                .put(WebServiceConstants.SaveClass.Parameters.DAY_OF_OCCURENCE, (pClass.getDayOfOccurence().equals(DayOfWeek.SUNDAY) ? 1 : pClass.getDayOfOccurence().getValue() + 1))
                .put(WebServiceConstants.SaveClass.Parameters.SUBJECT_ID, pClass.getSubjectId())
                .put(WebServiceConstants.SaveClass.Parameters.GROUP_IDS, pClass.getGroupIds())
                .put(WebServiceConstants.SaveClass.Parameters.BEGIN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(pClass.getBeginDate().getTime()))
                .put(WebServiceConstants.SaveClass.Parameters.END_DATE, new SimpleDateFormat("yyyy-MM-dd").format(pClass.getEndDate().getTime()))
                .put(WebServiceConstants.SaveClass.Parameters.BEGIN_TIME, new SimpleDateFormat("hh:mm:ss").format(pClass.getBeginTime().getTime()))
                .put(WebServiceConstants.SaveClass.Parameters.END_TIME, new SimpleDateFormat("hh:mm:ss").format(pClass.getEndTime().getTime()));

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetStudents() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetStudents.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject SaveStudent(Student student) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveStudent.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveStudent.Parameters.ID, student.getId() == -1 ? null : student.getId())
                .put(WebServiceConstants.SaveStudent.Parameters.DATE_OF_BIRTH, new SimpleDateFormat("yyyy-MM-dd").format(student.getDateOfBirth()))
                .put(WebServiceConstants.SaveStudent.Parameters.PLACE_OF_BIRTH, student.getPlaceOfBirth())
                .put(WebServiceConstants.SaveStudent.Parameters.ID_DOCUMENT_TYPE, student.getDocumentType().getId())
                .put(WebServiceConstants.SaveStudent.Parameters.DOCUMENT_NUMBER, student.getDocumentNumber())
                .put(WebServiceConstants.SaveStudent.Parameters.PESEL, student.getPESEL())
                .put(WebServiceConstants.SaveStudent.Parameters.CITY, student.getCity())
                .put(WebServiceConstants.SaveStudent.Parameters.REGION, student.getRegion())
                .put(WebServiceConstants.SaveStudent.Parameters.STREET, student.getStreet())
                .put(WebServiceConstants.SaveStudent.Parameters.HOUSE_NUMBER, student.getHouseNumber())
                .put(WebServiceConstants.SaveStudent.Parameters.FLAT_NUMBER, student.getFlatNumber())
                .put(WebServiceConstants.SaveStudent.Parameters.POSTAL_CODE, student.getPostalCode())
                .put(WebServiceConstants.SaveStudent.Parameters.COUNTRY, student.getCountry())
                .put(WebServiceConstants.SaveStudent.Parameters.REASON_FOR_LEAVE, student.getReasonForLeave());

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetTeachers() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetTeachers.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject SaveTeacher(Teacher teacher) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveTeacher.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveTeacher.Parameters.ID, teacher.getId() == -1 ? null : teacher.getId())
                .put(WebServiceConstants.SaveTeacher.Parameters.HIRE_DATE, new SimpleDateFormat("yyyy-MM-dd").format(teacher.getHireDate()))
                .put(WebServiceConstants.SaveTeacher.Parameters.ID_FORM_OF_EMPLOYMENT, teacher.getFormOfEmployment().getId());

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetParents() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetParents.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject GetParents(Boolean findStudentsIDs) {
        JSONObject json = new JSONObject();

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceConstants.GetParents.Parameters.FIND_STUDENTS_IDS, findStudentsIDs);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetParents.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject SaveParent(Parent parent) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveParent.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveParent.Parameters.ID, parent.getId() == -1 ? null : parent.getId())
                .put(WebServiceConstants.SaveParent.Parameters.CITY, parent.getCity())
                .put(WebServiceConstants.SaveParent.Parameters.REGION, parent.getRegion())
                .put(WebServiceConstants.SaveParent.Parameters.STREET, parent.getStreet())
                .put(WebServiceConstants.SaveParent.Parameters.HOUSE_NUMBER, parent.getHouseNumber())
                .put(WebServiceConstants.SaveParent.Parameters.FLAT_NUMBER, parent.getFlatNumber())
                .put(WebServiceConstants.SaveParent.Parameters.POSTAL_CODE, parent.getPostalCode())
                .put(WebServiceConstants.SaveParent.Parameters.COUNTRY, parent.getCountry())
                .put(WebServiceConstants.SaveParent.Parameters.E_MAIL, parent.getEMail())
                .put(WebServiceConstants.SaveParent.Parameters.PHONES, parent.getParentPhones());

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    public JSONObject GetAdmins() {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.GetAdmins.FUNCTION_NAME);

        return SendToWebService(json);
    }

    public JSONObject SaveParentChildren(Parent parent, Map<Student, RelationType> studentRelationTypeMap) {
        JSONObject json = new JSONObject();

        json.put(WebServiceConstants.FUNCTION_NAME, WebServiceConstants.SaveParentChildren.FUNCTION_NAME);

        JSONObject jsonParams = new JSONObject();

        jsonParams.put(WebServiceConstants.SaveParentChildren.Parameters.PARENT_ID, parent.getId());

        JSONArray studentsListArray = new JSONArray();
        for (Map.Entry<Student, RelationType> entry : studentRelationTypeMap.entrySet()) {
            JSONObject studentRelationTypeObject = new JSONObject();
            studentRelationTypeObject.put(WebServiceConstants.SaveParentChildren.Parameters.StudentRelationType.STUDENT_ID, entry.getKey().getId());
            studentRelationTypeObject.put(WebServiceConstants.SaveParentChildren.Parameters.StudentRelationType.RELATION_TYPE_ID, entry.getValue().getId());
            studentsListArray.put(studentRelationTypeObject);
        }

        jsonParams.put(WebServiceConstants.SaveParentChildren.Parameters.STUDENT_RELATION_TYPE, studentsListArray);

        json.put(WebServiceConstants.PARAMS, jsonParams);

        return SendToWebService(json);
    }

    private JSONObject SendToWebService(JSONObject... jsonObjects) {
        try {
            URL url = new URL(WEB_SERVICE_ADDRESS);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            ApplyTestCerificate(conn);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            jsonObjects[0].put(WebServiceConstants.MESSAGE_TOKEN, currentUserToken);
            osw.write(jsonObjects[0].toString());
            osw.flush();
            osw.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line).append("\r\n");
                }
                return new JSONObject(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Functions.SaveLog(e);
        }

        return null;
    }

    private void ApplyTestCerificate(HttpsURLConnection conn) {
        try {

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            Certificate ca;
            try (InputStream caInput = getClass().getResource("/raw/server.crt").openStream()) {
                ca = cf.generateCertificate(caInput);
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            conn.setSSLSocketFactory(context.getSocketFactory());
            conn.setHostnameVerifier((hostname, session) -> {
                if (hostname.equals("192.168.0.13")) return true;
                return HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
