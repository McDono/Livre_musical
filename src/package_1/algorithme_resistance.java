package package_1;

import java.util.Scanner;


public class algorithme_resistance {

	public static void main(String[] args) {
		
		//D�claration des variables
		int nombreDePage = 0;
		long resistParc[] = new long[100]; //A remplir
		Scanner sc = new Scanner(System.in);
		float tensionAlim = 3.3f; //tension max de lecture de la carte
		float tensionMax = 3.0f; //tension � la premi�re page
		byte tensionMin = 0; //tension � la derni�re page
		float tensions[] = new float[100];
		float resistEq[] = new float[100];
		float resistIdeal[] = new float[100];
		float resistanceFixe = 10f; //En kOhm
		
		
		
		//D�but du programme
		System.out.println("Algorithme de calcul des r�istances pour le livre musical");
		System.out.println("Nombre de page du livre : ");
		nombreDePage = sc.nextInt();
		sc.nextLine();
		System.out.println("Calcul des r�sistances pour un livre de " + nombreDePage + " pages.");
		
		calculerTensions(tensions, tensionMax, nombreDePage);
		calculerResistancesEq(resistEq, tensions, tensionAlim, resistanceFixe, nombreDePage);
		calculerResistancesPagesIdeales(nombreDePage, resistanceFixe, resistEq, resistIdeal);
		//calculerResistancesPagesNormalisee();
		
		//afficherAll();
		
		afficherPages(nombreDePage);
		afficherTensions(tensions, nombreDePage);
		afficherResistancesEq(resistEq, nombreDePage);
		afficherResistancesPagesIdeales(resistIdeal, nombreDePage);

	}
	
	//fonctions
	public static double arrondi(float A, int B)
	{
		return (double) ( (int) (A * Math.pow(10, B) + .5)) / Math.pow(10, B);
	}

	public static void calculerTensions(float tensions[], float tensionMax, int nombreDePage)
	{
		for(int i = 0; i < nombreDePage; i++)
		{
			tensions[i] = (float) arrondi(tensionMax / nombreDePage * (nombreDePage-i), 2);
		}
	}
	
	public static void calculerResistancesEq(float resistEq[], float tensions[], float tensionAlim, float resistanceFixe, int nombreDePage)
	{
		for(int i = 0; i < nombreDePage; i++)
		{
			resistEq[i] = (float) arrondi(resistanceFixe * (tensionAlim / tensions[i] - 1.0f), 2);
		}
	}
	
	public static void calculerResistancesPagesIdeales(int nombreDePage, float resistanceFixe, float resistEq[], float resistIdeal[])
	{
		//Calcul les r�sistances id�ales (non normalis�es, sans prendre en compte le parc de resistance poss�d�) de chaque pages n�cessaires pour atteindre les resistances �quivalentes en cumulant les pages
		resistIdeal[0] = resistEq[nombreDePage-1];
		for(int i = 1; i < nombreDePage; i++)
		{
			resistIdeal[i] = (float) arrondi((resistEq[nombreDePage - i] * resistEq[nombreDePage-1-i]) / (resistEq[nombreDePage - i] - resistEq[nombreDePage-1-i]), 2);
		}
	}

	public static void afficherPages(int nombreDePage)
	{
		System.out.print("\nPages :" + "\t\t\t\t");
		for(int i = 0; i < nombreDePage; i++)
		{
			System.out.print((i+1) + "\t");
		}
	}
	
	public static void afficherTensions(float tensions[], int nombreDePage)
	{
		System.out.print("\nTensions :" + "\t\t\t");
		for(int i = 0; i < nombreDePage; i++)
		{
			System.out.print(tensions[i] + "\t");
		}
	}
	
	public static void afficherResistancesEq(float resistEq[], int nombreDePage)
	{
		System.out.print("\nResistances Equivalentes : " + "\t");
		for(int i = 0; i < nombreDePage; i++)
		{
			System.out.print(resistEq[i] + "\t");
		}
	}
	
	public static void afficherResistancesPagesIdeales(float resistIdeal[], int nombreDePage)
	{
		System.out.print("\nResistances Pages Ideales : " + "\t");
		for(int i = 0; i < nombreDePage; i++)
		{
			System.out.print(resistIdeal[i] + "\t");
		}
	}
	
	
	public static float req2(float R1, float R2)
	{
		//Calcul la r�sistance �quivalente entre 2 r�sistances
		return (R1 * R2) / (R1 + R2);
	}
	
	public static float req3(float R1, float R2, float R3)
	{
		//Calcul la r�sistance �quivalente entre 3 r�sistances
		return 1.0f / (1.0f/R1 + 1.0f/R1 + 1.0f/R3);
	}
	

}
