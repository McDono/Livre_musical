package package_1;

import java.util.Scanner;

/*-------------------NOTE--------------------
 * 
 * Ecrire la fonction calculerResistancesPagesNormalisees
 * 		- Ecrire la fonction check précision
 * 		- Gérer le rappel de la fonction jusqu'à un résultat satisfaisant
 * 			>> Passer par des tableaux de resistances equivalent pour chaque page. Tout réecreire avec des tableaux
 * 				!!!! FAIRE UNE SAUVEGARDE AVANT !!! 
 * Optimiser les fonctions avec les metodes length afin d'eviter de passer l'argument nombreDePage
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
		ResistCouple ValeursNormalise[] = new ResistCouple[100];
		float resistanceFixe = 10f; //En kOhm
		float E6[] = {0.1f, 0.15f, 0.22f, 0.33f, 0.47f, 0.68f, 1.0f, 1.5f, 2.2f, 3.3f, 4.7f, 6.8f, 10.0f, 15.0f, 22.0f, 33.0f, 47.0f, 68.0f};
		
		
		
		//Debut du programme
		System.out.println("Algorithme de calcul des resistances pour le livre musical");
		System.out.println("Nombre de page du livre : ");
		nombreDePage = sc.nextInt();
		sc.nextLine();
		System.out.print("Calcul des resistances pour un livre de " + nombreDePage + " pages.");
		
		calculerTensions(tensions, tensionMax, nombreDePage);
		calculerResistancesEq(resistEq, tensions, tensionAlim, resistanceFixe, nombreDePage);
		calculerResistancesPagesIdeales(nombreDePage, resistanceFixe, resistEq, resistIdeal);
		//calculerResistancesPagesNormalisees();
	
		afficherAll(nombreDePage, tensions, resistEq, resistIdeal);
		
		//Zone de test
		ResistCouple test = new ResistCouple();
		test = resistanceEqPara(44.0f, E6);
		System.out.println(test.afficherCouple());
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
	
	public static void calculerResistancesPagesNormalisees(int nombreDePage, float parcResistance[], float resistIdeal[], ResistCouple ValeursNormalise[], )
	{
		//Remplis le tableau de resistance avec de VN du parc
		for (int i = 0; i < nombreDePage; i++)
		{
			do //Pour chacune des resistance, tant que la valeur approchée est trop eloigné (seuil de précision) de la valeur théorique, on recalcul une équivalence
			{
				ValeursNormalise[i] = resistanceEqPara(resistIdeal[i], parcResistance);
			}while();
			
			ValeursNormalise[i] = resistanceEqPara(resistIdeal[i], parcResistance);
			if (checkPrecision() == false)
			{
				
			}
				
				
		}
	
		
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
		{
			i++;
		}
		
		couple.R1 = parcResistance[i]; //On stocke cette VN
		
		if (parcResistance[i] > req) //Si la VN est sup à req, on applique la méthode pour trouver 2 resistances parrallèles
			couple.R2 = arrondi(couple.R1 * req / (couple.R1 + req), 2);
			
		return couple; //Si req est une VN du parc, elle est retourné en R1 du couple, et R2 du couple reste à 0
	}
	
	public static void afficherAll(int nombreDePage, float tensions[], float resistEq[], float resistIdeal[])
	{
		afficherPages(nombreDePage);
		afficherTensions(tensions);
		afficherResistancesEq(resistEq, nombreDePage);
		afficherResistancesPagesIdeales(resistIdeal, nombreDePage);
	}

	public static void afficherPages(int nombreDePage)
	{
		System.out.print("\nPages :" + "\t\t\t\t");
		for(int i = 0; i < nombreDePage; i++)
		{
			System.out.print((i+1) + "\t");
		}
	}
	
	public static void afficherTensions(float tensions[])
	{
		System.out.print("\nTensions :" + "\t\t\t");
		byte i = 0;
		while(tensions[i] > 0)
		{
			System.out.print(tensions[i] + "\t");
			i++;
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
		//Calcul la rï¿½sistance ï¿½quivalente entre 2 rï¿½sistances
		return (R1 * R2) / (R1 + R2);
	}
	
	
	
	public static float req3(float R1, float R2, float R3)
	{
		//Calcul la rï¿½sistance ï¿½quivalente entre 3 rï¿½sistances
		return 1.0f / (1.0f/R1 + 1.0f/R1 + 1.0f/R3);
	}
	
	public static float arrondi(float valeur, int nombreDeDecimal)
	{
		//Arrondi la valeur avec le nombre de decimal indiqué
		return (float) (Math.round(valeur * Math.pow(10, nombreDeDecimal)) / Math.pow(10, nombreDeDecimal));
	}
	

}
