//Le workspace est le suivant : C:\Users\McDon\Documents\Projets\Projet_test

package package_1;

import java.util.Scanner;

/*-------------------NOTE--------------------
 * 
 * Ameliorer la precision des resisstances equivalentes
 * Checker si une seule résitance suffit pour une page
 * Optimiser les fonctions avec les metodes length afin d'eviter de passer l'argument nombreDePage (ligne ~210)
 * 
 * 
 * 
 * 
 * 
 */

public class algorithme_resistance {

	public static void main(String[] args) 
	{
		
		//Delaration des variables
		int nombreDePage = 0;
		long resistParc[] = new long[100]; //A remplir
		Scanner sc = new Scanner(System.in);
		float tensionAlim = 3.3f; //tension max de lecture de la carte
		float tensionMax = 3.0f; //tension ï¿½ la premiï¿½re page
		byte tensionMin = 0; //tension ï¿½ la derniï¿½re page
		float tensions[] = new float[100];
		float resistEq[] = new float[100];
		float resistIdeal[] = new float[100];
		//ResistCouple ValeursNormalise[] = new ResistCouple[100];
		float ValeursNormalise[][] = new float[100][4]; //4 représente lle nombre max de resistance par page
		float resistanceFixe = 1f; //En kOhm
		float seuilPrecision = 0.5f; //En pourcentage
		float E6[] = {0.1f, 0.15f, 0.22f, 0.33f, 0.47f, 0.68f, 1.0f, 1.5f, 2.2f, 3.3f, 4.7f, 6.8f, 10.0f, 15.0f, 22.0f, 33.0f, 47.0f, 68.0f, 100.0f, 150.0f, 220.0f, 330.0f, 470.0f, 680.0f, 1000.0f, 1500.0f, 2200.0f, 3300.0f, 4700.0f, 6800.0f};
		float parcDiscret[] = {0.00039f, 0.0022f, 0.00887f, 0.047f, 0.075f, 0.120f, 0.150f, 0.180f, 0.270f, 0.330f, 0.47f, 0.62f, 0.86f, 0.75f, 0.82f, 1.2f, 1.5f, 1.8f, 2.2f, 3.3f, 3.9f, 4.3f, 4.7f, 5.6f, 6.2f, 6.8f, 8.2f, 12.0f, 15.0f, 18.0f, 20.0f, 22.0f, 27.0f, 33.0f, 43.0f, 62.0f, 75.0f, 100.0f, 120.0f, 150.0f, 180.0f, 220.0f, 270.0f, 390.0f, 470.0f, 2200.0f, 4700.0f, 10000.0f};
		float parcCMS[] = {0.0015f, 0.0033f, 0.0047f, 0.01f, 0.022f, 0.033f, 0.047f, 0.056f, 0.068f, 0.082f, 0.1f, 0.22f, 0.33f, 0.47f, 0.82f, 1.0f, 2.2f, 2.7f, 3.3f, 4.7f, 6.8f, 8.2f, 10.0f, 18.0f, 22.0f, 27.0f, 33.0f, 39.0f, 47.0f, 56.0f, 68.0f, 82.0f, 100.0f, 330.0f,};
		
		//Debut du programme
		System.out.println("Algorithme de calcul des resistances pour le livre musical");
		System.out.println("Nombre de page du livre : ");
		nombreDePage = sc.nextInt();
		sc.nextLine();
		System.out.print("Calcul des resistances pour un livre de " + nombreDePage + " pages.");
		
		calculerTensions(tensions, tensionMax, nombreDePage);
		calculerResistancesEq(resistEq, tensions, tensionAlim, resistanceFixe, nombreDePage);
		calculerResistancesPagesIdeales(nombreDePage, resistanceFixe, resistEq, resistIdeal);
		calculerResistancesPagesNormalisees(nombreDePage, parcDiscret, resistIdeal, ValeursNormalise, seuilPrecision);
	
		afficherAll(nombreDePage, tensions, resistEq, resistIdeal);
		afficherResistancesPagesNormalisees(nombreDePage, ValeursNormalise);
		
		//Zone de test
		/*ResistCouple test = new ResistCouple();
		test = resistanceEqPara(44.0f, E6);
		System.out.println(test.afficherCouple());*/
		//Fin zone de test

	}
	
	//fonctions
	/*public static double arrondi(float A, int B)
	{
		return (double) ( (int) (A * Math.pow(10, B) + .5)) / Math.pow(10, B);
	}*/

	public static void calculerTensions(float tensions[], float tensionMax, int nombreDePage)
	{
		for(int i = 0; i < nombreDePage; i++)
		{
			tensions[i] = arrondi(tensionMax / nombreDePage * (nombreDePage-i), 2);
		}
	}
	
	public static void calculerResistancesEq(float resistEq[], float tensions[], float tensionAlim, float resistanceFixe, int nombreDePage)
	{
		for(int i = 0; i < nombreDePage; i++)
		{
			resistEq[i] = arrondi(resistanceFixe * (tensionAlim / tensions[i] - 1.0f), 2);
		}
	}
	
	public static void calculerResistancesPagesIdeales(int nombreDePage, float resistanceFixe, float resistEq[], float resistIdeal[])
	{
		//Calcul les resistances ideales (non normalisess, sans prendre en compte le parc de resistance possedee) de chaque pages necessaires pour atteindre les resistances ï¿½quivalentes en cumulant les pages
		resistIdeal[0] = resistEq[nombreDePage-1];
		for(int i = 1; i < nombreDePage; i++)
		{
			resistIdeal[i] = arrondi((resistEq[nombreDePage - i] * resistEq[nombreDePage-1-i]) / (resistEq[nombreDePage - i] - resistEq[nombreDePage-1-i]), 2);
		}
	}
	
	public static float normalize(float res, float parcResistance[])
	{
		//Renvoie la VN du parc la plus proche de la resistance voulue
		float ecart = 0.0f;
		float old_ecart = 100000000000000000.0f;
		float bestVN = 0.0f;
		
		for (float vn : parcResistance)
		{
			ecart = Math.abs(res - vn);
			if (ecart < old_ecart)
			{
				bestVN = vn;
				old_ecart = ecart;
			}
		}
		return bestVN;
	}
	
	public static float normalizeUp(float res, float parcResistance[])
	{
		//Renvoie la VN du parc égale ou directement supérieur à la résistance voulue
		int i = 0;
		while (parcResistance[i] < res)
			i++;
		return parcResistance[i];
	}
	
	public static void calculerResistancesPagesNormalisees(int nombreDePage, float parcResistance[], float resistIdeal[], float ValeursNormalise[][], float seuilPrecision)
	{
		//Remplis le tableau de resistance avec de VN du parc	
		int j = 1;
		float req;
		ResistCouple couple = new ResistCouple();
		for (int i = 0; i < nombreDePage; i++)
		{	
			couple = resistanceEqPara(resistIdeal[i], parcResistance);
			ValeursNormalise[i][0] = couple.R1;	
			ValeursNormalise[i][1] = couple.R2;
			req = req2(ValeursNormalise[i][0], ValeursNormalise[i][1]); //On stock la resistance equivalente totale de la page
			while (checkPrecision(resistIdeal[i], req, seuilPrecision) == false && j < ValeursNormalise[i].length - 1) //Tant que la précision est trop faible, et que la limite de resistance par page n'est pas atteinte
			{
				j++;
				ValeursNormalise[i][j] = resistanceEqPara(resistIdeal[i], parcResistance, req).R2; //On ajoute une resistance parallèle
				req = req2(req, ValeursNormalise[i][j]); //On actualise la resistance equivalente totale de la page
			}
		}
	}
	
	public static boolean checkPrecision(float reqTheorique, float reqEffective, float seuilPrecision)
	{
		if ( Math.abs(reqEffective - reqTheorique) / reqTheorique * 100.0f <= seuilPrecision)
			return true;
		else
			return false;
	}
	public static ResistCouple resistanceEqPara(float req, float parcResistance[])
	{
		//Renvoie la paire de resistance necessaire à l'obtention de Req
		ResistCouple couple = new ResistCouple();
		int i = 0;
		
		for (float res : parcResistance) //Si une VN du parc est egal au double de la req, on renvoie un couple de 2 fois cette VN
		{
			if (res == req * 2.0f)
			{
				couple.R1 = couple.R2 = res;
				return couple;
			}
		}
		
		while (parcResistance[i] < req)//On se position à la VN du parc égale ou directmeent supérieur à req
			i++;
		
		couple.R1 = parcResistance[i]; //On stocke cette VN
		
		if (parcResistance[i] > req) //Si la VN est sup à req, on applique la méthode pour trouver 2 resistances parrallèles
			couple.R2 = normalizeUp(couple.R1 * req / (couple.R1 - req), parcResistance);
			
		return couple; //Si req est une VN du parc, elle est retourné en R1 du couple, et R2 du couple reste à 0
	}
	
	public static ResistCouple resistanceEqPara(float req, float parcResistance[], float R1)
	{
		//Renvoie la paire de resistance necessaire à l'obtention de Req A PARTIR D'UNE RESISTANCE DONNEE
		ResistCouple couple = new ResistCouple();
		couple.R1 = R1;
		
		if (req == R1)
			couple.R2 = 0;
		else if (req == R1 / 2.0f)
			couple.R1 = couple.R2 = R1;
		else
			couple.R2 = normalizeUp(R1 * req / (R1 - req), parcResistance);
			
		return couple; 
	}
	
	public static void afficherAll(int nombreDePage, float tensions[], float resistEq[], float resistIdeal[])
	{
		afficherPages(nombreDePage);
		afficherTensions(tensions, nombreDePage);
		afficherResistancesEq(resistEq, nombreDePage);
		afficherResistancesPagesIdeales(resistIdeal, nombreDePage);
	}

	public static void afficherPages(int nombreDePage)
	{
		System.out.print("\nPages :" + "\t\t\t\t");
		for(int i = 0; i < nombreDePage; i++)
			System.out.print((i+1) + "\t");
	}
	
	public static void afficherTensions(float tensions[], int nombreDePage)
	{
		System.out.print("\nTensions :" + "\t\t\t");
		for(int i = 0; i < nombreDePage; i++)
			System.out.print(tensions[i] + "\t");
	}
	
	public static void afficherResistancesEq(float resistEq[], int nombreDePage)
	{
		System.out.print("\nResistances Equivalentes : " + "\t");
		for(int i = 0; i < nombreDePage; i++)
			System.out.print(resistEq[i] + "\t");
	}
	
	public static void afficherResistancesPagesIdeales(float resistIdeal[], int nombreDePage)
	{
		System.out.print("\nResistances Pages Ideales : " + "\t");
		for(int i = 0; i < nombreDePage; i++)
			System.out.print(resistIdeal[i] + "\t");
		System.out.print('\n');
	}
	
	public static void afficherResistancesPagesNormalisees(int nombreDePage, float ValeursNormalise[][])
	{
		//Affiche pour les resistances VN du parc a mettre en parallèle pour chaque page
		int j = 0;
		float req = 0.0f;
		
		for (int i = 0; i < nombreDePage; i++)
		{
			System.out.print("Page "+(i+1)+" : ");
			System.out.print(ValeursNormalise[i][0]);
			req = ValeursNormalise[i][0];
			
			j = 1;
			while (j < ValeursNormalise[i].length && ValeursNormalise[i][j] > 0)
			{
				System.out.print(" // "+ValeursNormalise[i][j]);
				req = req2(req, ValeursNormalise[i][j]);
				j++;
			}
			
			System.out.println(" = "+arrondi(req,2));
		}
	}

	
	public static float req2(float R1, float R2)
	{
		//Calcul la resistance equivalente entre 2 resistances
		return (R1 * R2) / (R1 + R2);
	}
	
	
	
	public static float req3(float R1, float R2, float R3)
	{
		//Calcul la resistance equivalente entre 3 resistances
		return 1.0f / (1.0f/R1 + 1.0f/R1 + 1.0f/R3);
	}
	
	public static float arrondi(float valeur, int nombreDeDecimal)
	{
		//Arrondi la valeur avec le nombre de decimal indiqué
		return (float) (Math.round(valeur * Math.pow(10, nombreDeDecimal)) / Math.pow(10, nombreDeDecimal));
	}
	

}
