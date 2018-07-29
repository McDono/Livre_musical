package package_1;

public class ResistCouple {
	float R1;
	float R2;
	
	public ResistCouple() //Constructeur
	{
		R1 = 0.0f;
		R2 = 0.0f;
	}
	
	public String afficherCouple()
	{
		return "( "+R1+", "+R2+" )";
	}
	
	public float req()
	{
		return R1 * R2 / (R1 + R2);
	}
	

}
