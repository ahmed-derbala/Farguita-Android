package tn.esprit.farguita.app;

public class AppConfig {
	//APIKEY
	public static String API_KEY = "AIzaSyCaQhS9AM_Uj-UMwQMA8Ec7fEoT-BbrnaA";
	// Server url
	public static String URL_SERVER = "http://192.168.1.3/";

	// Server user login url
	public static String URL_LOGIN = URL_SERVER+"FarguitaServer/login.php";

	// Server user register url
	public static String URL_REGISTER = URL_SERVER+"FarguitaServer/register.php";

	// send and receive profile pic url
	public static String URL_SEND_PROFILE_PHOTO = URL_SERVER+"FarguitaServer/sendAndReceiveProfilePhoto.php?apicall=uploadpic";

	// Server ajouter favoris url
	public static String URL_AJOUTER_FAVORIS = URL_SERVER+"FarguitaServer/addfavoris.php";

	//get favoris by user id
	public static String URL_GET_FAVORIS_BY_USERNAME = URL_SERVER+"FarguitaServer/getfavoris.php";


	//get favoris by user id
	public static String URL_GET_USER_COMMENTS = URL_SERVER+"FarguitaServer/getUserComments.php";

	// Server ajouter comment url
	public static String URL_AJOUTER_COMMENT = URL_SERVER+"FarguitaServer/addcomment.php";

	// Server delete favoris url
	public static String URL_SUPPRIMER_FAVORIS = URL_SERVER+"FarguitaServer/deletefavoris.php";
	// Server get promotion url
	public static String URL_GET_PROMOTION = URL_SERVER+"FarguitaServer/getpromotions.php";



}
