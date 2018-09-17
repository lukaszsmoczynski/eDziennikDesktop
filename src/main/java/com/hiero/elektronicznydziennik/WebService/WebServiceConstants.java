package com.hiero.elektronicznydziennik.WebService;

/**
 * Created by Hiero on 14.12.2017.
 */

public class WebServiceConstants {
    static final String FUNCTION_NAME = "FUNCTION_NAME";
    static final String MESSAGE_TOKEN = "TOKEN";
    static final String PARAMS = "PARAMS";

    public class Login {
        static final String FUNCTION_NAME = "Login";

        class Parameters {
            static final String LOGIN = "LOGIN";
            static final String PASSWORD = "PASSWORD";
        }

        public class Result {
            public static final String RESULT = "RESULT";
            static final String USER_TOKEN_RESULT = "USER_TOKEN";
            public static final String USER_ID = "USER_ID";
            public static final String IS_STUDENT = "IS_STUDENT";
            public static final String IS_TEACHER = "IS_TEACHER";
            public static final String IS_PARENT = "IS_PARENT";
            public static final String IS_ADMIN = "IS_ADMIN";
        }
    }

    public class Logout {
        static final String FUNCTION_NAME = "Logout";

        public class Parameters {
        }

        class Result {
            static final String RESULT = "RESULT";
        }
    }

    public class GetChildrenOfParent {
        static final String FUNCTION_NAME = "GetChildrenOfParent";

        class Parameters {
            static final String PARENT_ID = "PARENT_ID";
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String CHILDREN = "STUDENT_RELATION_TYPE";

            public class Children {
                public static final String CHILDREN_ID = "CHILDREN_ID";
                public static final String CHILDREN_NAME = "CHILDREN_NAME";
                public static final String CHILDREN_SURNAME = "CHILDREN_SURNAME";
            }
        }
    }

    public class GetStudentNotes {
        public static final String FUNCTION_NAME = "GetStudentNotes";

        public class Parameters {
            public static final String STUDENT_ID = "STUDENT_ID";
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String STUDENT_NOTES = "STUDENT_NOTES";

            public class StudentNotes {
                public static final String CLASS_NAME = "CLASS_NAME";
                public static final String NOTES = "NOTES";

                public class Notes {
                    public static final String NOTE_VALUE = "NOTE_VALUE";
                    public static final String GIVER = "GIVER";
                    public static final String DATE_OF_OCCURENCE = "DATE_OF_OCCURENCE";
                    public static final String REASON = "REASON";
                    public static final String NOTE_ID = "NOTE_ID";
                }
            }
        }
    }

    public class DeleteNote {
        public static final String FUNCTION_NAME = "DeleteNote";

        public class Parameters {
            public static final String NOTE_ID = "NOTE_ID";
            public static final String REASON = "REASON";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetSchedule {
        public static final String FUNCTION_NAME = "GetSchedule";

        public class Parameters {
            public static final String USER_ID = "USER_ID";
            public static final String USER_TYPE = "USER_TYPE";
            public static final String BEGIN_DATE = "BEGIN_DATE";
            public static final String COUNT = "COUNT";
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String SCHEDULE = "SCHEDULE";

            public class Schedule {
                public static final String DATE = "DATE";
                public static final String CLASSES = "CLASSES";

                public class Classes {
                    public static final String CLASS_ID = "CLASS_ID";
                    public static final String CLASS_NAME = "CLASS_NAME";
                    public static final String BEGIN_TIME = "BEGIN_TIME";
                    public static final String END_TIME = "END_TIME";
                    public static final String TEACHER = "TEACHER";
                    public static final String GROUPS = "GROUPS";
                    public static final String PRESENT = "PRESENT";
                    public static final String INSTANCE_ID = "INSTANCE_ID";
                }
            }
        }
    }

    public class StartClass {
        public static final String FUNCTION_NAME = "StartClass";

        public class Parameters {
            public static final String CLASS_ID = "CLASS_ID";
            public static final String TOPIC = "TOPIC";
            public static final String DATE = "DATE";
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String CLASS_INSTANCE_ID = "CLASS_INSTANCE_ID";
        }
    }

    public class EndClass {
        public static final String FUNCTION_NAME = "EndClass";

        public class Parameters {
            public static final String CLASS_INSTANCE_ID = "CLASS_INSTANCE_ID";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetStudentsOfClassInstance {
        public static final String FUNCTION_NAME = "GetStudentsOfClassInstance";

        public class Parameters {
            public static final String CLASS_INSTANCE_ID = "CLASS_INSTANCE_ID";
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String STUDENTS = "STUDENTS";

            public class Students {
                public static final String ID = "ID";
                public static final String NAME = "NAME";
                public static final String SURNAME = "SURNAME";
            }
        }
    }

    public class SaveAttendance {
        public static final String FUNCTION_NAME = "SaveAttendance";

        public class Parameters {
            public static final String CLASS_INSTANCE_ID = "CLASS_INSTANCE_ID";
            public static final String STUDENTS = "STUDENTS";

            public class Students {
                public static final String ID = "ID";
                public static final String PRESENT = "PRESENT";
            }
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class IssueNote {
        public static final String FUNCTION_NAME = "IssueNote";

        public class Parameters {
            public static final String CLASS_INSTANCE_ID = "CLASS_INSTANCE_ID";
            public static final String NOTE_VALUE = "NOTE_VALUE";
            public static final String REASON = "REASON";
            public static final String STUDENT_IDS = "STUDENT_IDS";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetUsers {
        public static final String FUNCTION_NAME = "GetUsers";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String USERS = "USERS";

            public class Users {
                public static final String ID = "ID";
                public static final String LOGIN = "LOGIN";
                public static final String PASSWORD = "PASSWORD";
                public static final String NAME = "NAME";
                public static final String SURNAME = "SURNAME";
                public static final String TEACHER = "TEACHER";
                public static final String STUDENT = "STUDENT";
                public static final String PARENT = "PARENT";
                public static final String ADMIN = "ADMIN";
            }
        }
    }

    public class SaveUser {
        public static final String FUNCTION_NAME = "SaveUser";

        public class Parameters {
            public static final String ID = "ID";
            public static final String LOGIN = "LOGIN";
            public static final String PASSWORD = "PASSWORD";
            public static final String NAME = "NAME";
            public static final String SURNAME = "SURNAME";
            public static final String TEACHER = "TEACHER";
            public static final String STUDENT = "STUDENT";
            public static final String PARENT = "PARENT";
            public static final String ADMIN = "ADMIN";
            public static final String HIRE_DATE = "HIRE_DATE";
            public static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetModules {
        public static final String FUNCTION_NAME = "GetModules";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String MODULES = "MODULES";

            public class Modules {
                public static final String ID = "ID";
                public static final String NAME = "NAME";
            }
        }
    }

    public class GetGroups {
        public static final String FUNCTION_NAME = "GetGroups";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String GROUPS = "GROUPS";

            public class Groups {
                public static final String ID = "ID";
                public static final String NAME = "NAME";
                public static final String CREATION_YEAR = "CREATION_YEAR";
                public static final String PARENT_GROUP_ID = "PARENT_GROUP_ID";
            }
        }
    }

    public class SaveGroup {
        public static final String FUNCTION_NAME = "SaveGroup";

        public class Parameters {
            public static final String ID = "ID";
            public static final String NAME = "NAME";
            public static final String CREATION_YEAR = "CREATION_YEAR";
            public static final String PARENT_GROUP_ID = "PARENT_GROUP_ID";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetDocumentTypes {
        public static final String FUNCTION_NAME = "GetDocumentTypes";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String DOCUMENT_TYPES = "DOCUMENT_TYPES";

            public class DocumentTypes {
                public static final String ID = "ID";
                public static final String NAME = "NAME";
            }
        }
    }

    public class GetFormsOfEmployment {
        public static final String FUNCTION_NAME = "GetFormsOfEmployment";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String FORMS_OF_EMPLOYMENT = "FORMS_OF_EMPLOYMENT";

            public class FormsOfEmployment {
                public static final String ID = "ID";
                public static final String NAME = "NAME";
            }
        }
    }

    public class GetSubjects {
        public static final String FUNCTION_NAME = "GetSubjects";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String SUBJECTS = "SUBJECTS";

            public class Subjects {
                public static final String ID = "ID";
                public static final String NAME = "NAME";
            }
        }
    }

    public class GetRelationTypes {
        public static final String FUNCTION_NAME = "GetRelationTypes";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String RELATION_TYPES = "RELATION_TYPES";

            public class RelationTypes {
                public static final String ID = "ID";
                public static final String NAME = "NAME";
            }
        }
    }

    public class SaveSubject {
        public static final String FUNCTION_NAME = "SaveSubject";

        public class Parameters {
            public static final String ID = "ID";
            public static final String NAME = "NAME";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class SaveRelationType {
        public static final String FUNCTION_NAME = "SaveRelationType";

        public class Parameters {
            public static final String ID = "ID";
            public static final String NAME = "NAME";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class SaveDocumentType {
        public static final String FUNCTION_NAME = "SaveDocumentType";

        public class Parameters {
            public static final String ID = "ID";
            public static final String NAME = "NAME";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class SaveFormOfEmployment {
        public static final String FUNCTION_NAME = "SaveFormOfEmployment";

        public class Parameters {
            public static final String ID = "ID";
            public static final String NAME = "NAME";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class SaveClass {
        public static final String FUNCTION_NAME = "SaveClass";

        public class Parameters {
            public static final String ID = "ID";
            public static final String NAME = "NAME";
            public static final String TEACHER_ID = "TEACHER_ID";
            public static final String SUBJECT_ID = "SUBJECT_ID";
            public static final String DAY_OF_OCCURENCE = "DAY_OF_OCCURENCE";
            public static final String GROUP_IDS = "GROUP_IDS";
            public static final String BEGIN_TIME = "BEGIN_TIME";
            public static final String END_TIME = "END_TIME";
            public static final String BEGIN_DATE = "BEGIN_DATE";
            public static final String END_DATE = "END_DATE";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetStudents {
        public static final String FUNCTION_NAME = "GetStudents";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String STUDENTS = "STUDENTS";

            public class Students {
                public static final String ID = "ID";
                public static final String LOGIN = "LOGIN";
                public static final String NAME = "NAME";
                public static final String SURNAME = "SURNAME";
                public static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
                public static final String PLACE_OF_BIRTH = "PLACE_OF_BIRTH";
                public static final String ID_DOCUMENT_TYPE = "ID_DOCUMENT_TYPE";
                public static final String TYPE_OF_DOCUMENT = "TYPE_OF_DOCUMENT";
                public static final String DOCUMENT_NUMBER = "DOCUMENT_NUMBER";
                public static final String PESEL = "PESEL";
                public static final String COUNTRY = "COUNTRY";
                public static final String CITY = "CITY";
                public static final String REGION = "REGION";
                public static final String STREET = "STREET";
                public static final String HOUSE_NUMBER = "HOUSE_NUMBER";
                public static final String FLAT_NUMBER = "FLAT_NUMBER";
                public static final String POSTAL_CODE = "POSTAL_CODE";
                public static final String REASON_FOR_LEAVE = "REASON_FOR_LEAVE";
            }
        }
    }

    public class SaveStudent {
        public static final String FUNCTION_NAME = "SaveStudent";

        public class Parameters {
            public static final String ID = "ID";
            public static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
            public static final String PLACE_OF_BIRTH = "PLACE_OF_BIRTH";
            public static final String ID_DOCUMENT_TYPE = "ID_DOCUMENT_TYPE";
            public static final String DOCUMENT_NUMBER = "DOCUMENT_NUMBER";
            public static final String PESEL = "PESEL";
            public static final String COUNTRY = "COUNTRY";
            public static final String CITY = "CITY";
            public static final String REGION = "REGION";
            public static final String STREET = "STREET";
            public static final String HOUSE_NUMBER = "HOUSE_NUMBER";
            public static final String FLAT_NUMBER = "FLAT_NUMBER";
            public static final String POSTAL_CODE = "POSTAL_CODE";
            public static final String REASON_FOR_LEAVE = "REASON_FOR_LEAVE";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetTeachers {
        public static final String FUNCTION_NAME = "GetTeachers";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String TEACHERS = "TEACHERS";

            public class Teachers {
                public static final String ID = "ID";
                public static final String LOGIN = "LOGIN";
                public static final String NAME = "NAME";
                public static final String SURNAME = "SURNAME";
                public static final String HIRE_DATE = "HIRE_DATE";
                public static final String ID_FORM_OF_EMPLOYMENT = "ID_FORM_OF_EMPLOYMENT";
                public static final String FORM_OF_EMPLOYMENT = "FORM_OF_EMPLOYMENT";
            }
        }
    }

    public class SaveTeacher {
        public static final String FUNCTION_NAME = "SaveTeacher";

        public class Parameters {
            public static final String ID = "ID";
            public static final String HIRE_DATE = "HIRE_DATE";
            public static final String ID_FORM_OF_EMPLOYMENT = "ID_FORM_OF_EMPLOYMENT";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetParents {
        public static final String FUNCTION_NAME = "GetParents";

        public class Parameters {
            public static final String FIND_STUDENTS_IDS = "FIND_STUDENTS_IDS";
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String PARENTS = "PARENTS";

            public class Parents {
                public static final String ID = "ID";
                public static final String LOGIN = "LOGIN";
                public static final String NAME = "NAME";
                public static final String SURNAME = "SURNAME";
                public static final String COUNTRY = "COUNTRY";
                public static final String CITY = "CITY";
                public static final String REGION = "REGION";
                public static final String STREET = "STREET";
                public static final String HOUSE_NUMBER = "HOUSE_NUMBER";
                public static final String FLAT_NUMBER = "FLAT_NUMBER";
                public static final String POSTAL_CODE = "POSTAL_CODE";
                public static final String E_MAIL = "E_MAIL";
                public static final String PHONES = "PHONES";
                public static final String STUDENTS_RELATIONS = "STUDENTS_RELATIONS";

                public class StudentsRelations {
                    public static final String STUDENT_ID = "STUDENT_ID";
                    public static final String RELATION_TYPE_ID = "RELATION_TYPE_ID";
                }
            }
        }
    }

    public class SaveParent {
        public static final String FUNCTION_NAME = "SaveParent";

        public class Parameters {
            public static final String ID = "ID";
            public static final String COUNTRY = "COUNTRY";
            public static final String CITY = "CITY";
            public static final String REGION = "REGION";
            public static final String STREET = "STREET";
            public static final String HOUSE_NUMBER = "HOUSE_NUMBER";
            public static final String FLAT_NUMBER = "FLAT_NUMBER";
            public static final String POSTAL_CODE = "POSTAL_CODE";
            public static final String E_MAIL = "E_MAIL";
            public static final String PHONES = "PHONES";
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }

    public class GetAdmins {
        public static final String FUNCTION_NAME = "GetAdmins";

        public class Parameters {
        }

        public class Result {
            public static final String RESULT = "RESULT";
            public static final String ADMINS = "ADMINS";

            public class Admins {
                public static final String ID = "ID";
                public static final String LOGIN = "LOGIN";
                public static final String NAME = "NAME";
                public static final String SURNAME = "SURNAME";
            }
        }
    }

    public class SaveParentChildren {
        public static final String FUNCTION_NAME = "SaveParentChildren";

        public class Parameters {
            public static final String PARENT_ID = "PARENT_ID";
            public static final String STUDENT_RELATION_TYPE = "STUDENT_RELATION_TYPE";

            public class StudentRelationType {
                public static final String STUDENT_ID = "STUDENT_ID";
                public static final String RELATION_TYPE_ID = "RELATION_TYPE_ID";
            }
        }

        public class Result {
            public static final String RESULT = "RESULT";
        }
    }
}
