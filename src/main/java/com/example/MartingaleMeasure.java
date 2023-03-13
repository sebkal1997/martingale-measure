package com.example;

import java.util.Arrays;
import java.util.Scanner;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

public class MartingaleMeasure {
    static final int N = 2;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int numberOfAssets = 1;
        int numberOfScenarios = 2;
        do {
            if (numberOfAssets < numberOfScenarios - 1) {
                System.out.println("Too many scenarios for so many assets.");
            }
            if (numberOfScenarios - 1 < numberOfAssets) {
                System.out.println("Provide more scenarios or less assets.");
            }
            System.out.println("Enter how many assets do you want to specify: ");
            numberOfAssets = sc.nextInt();
            System.out.println("Enter how many scenarios do you want to specify: ");
            numberOfScenarios = sc.nextInt();
        } while (numberOfScenarios - 1 < numberOfAssets || numberOfAssets < numberOfScenarios - 1);

        double[][] priceOfTheAssets = new double[numberOfAssets + 1][numberOfScenarios];
        for (double[] row : priceOfTheAssets)
            Arrays.fill(row, 1.0);
        double[] priceTimesDiscountRate = new double[numberOfAssets + 1];
        double discountRate;
        System.out.println("Wpisz ceny aktywów: ");
        for (int i = 0; i < numberOfAssets; i++) {
            for (int j = 0; j < numberOfScenarios + 1; j++) {
                if (j == 0) {
                    System.out.println("Enter prices for " + (i + 1) + " asset");
                    System.out.println("S" + j);
                    priceTimesDiscountRate[i] = sc.nextDouble();
                } else {
                    System.out.println("S" + (i + 1) + "(w" + j + ")");
                    priceOfTheAssets[i][j - 1] = sc.nextDouble();
                }
            }
        }
        System.out.println("Enter the discount rate in percent: ");
        discountRate = sc.nextDouble() / 100;
        for (int k = 0; k < numberOfAssets + 1; k++) {
            if (k == numberOfAssets) {
                priceTimesDiscountRate[k] = 1;
                continue;
            }
            priceTimesDiscountRate[k] = priceTimesDiscountRate[k] * (discountRate + 1);
        }

        double determinantOfMatrix = determinantOfMatrix(priceOfTheAssets, numberOfScenarios);

        if (determinantOfMatrix == 0) {
            System.out.println("There are no or infinite solutions");
        } else {
            RealMatrix coefficients = new Array2DRowRealMatrix(priceOfTheAssets, false);
            DecompositionSolver solver = new SingularValueDecomposition(coefficients).getSolver();
            RealVector constants = new ArrayRealVector(priceTimesDiscountRate, false);
            RealVector solution = solver.solve(constants);
            if (solution.getMinValue() <= 0) {
                System.out.println("Martingale measure does not exists");
            } else {
                System.out.println("Martingale measure is: ");
                for (int i = 0; i < numberOfScenarios; i++) {
                    System.out.println("p" + (i + 1) + ": " + solution.getEntry(i));
                }
            }
        }

        System.out.println("Enter any key to continue...");
        sc.next();
        sc.close();
    }

    private static void getCofactor(double mat[][], double temp[][], int p, int q, int n) {
        int i = 0, j = 0;

        // Looping for each element of the matrix.
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // Copying into temporary matrix only those element which are not in given row and column.
                if (row != p && col != q) {
                    temp[i][j++] = mat[row][col];
                    // Row is filled, so increase row index and reset col index.
                    if (j == n - 1) {
                        j = 0;
                        i++;
                    }
                }
            }
        }
    }

    private static double determinantOfMatrix(double mat[][], int n) {
        double D = 0; // Initialize result

        // Base case : if matrix contains single element
        if (n == 1) {
            return mat[0][0];
        }
        // To store cofactors
        double temp[][] = new double[N][N];

        // To store sign multiplier
        int sign = 1;

        // Iterate for each element of first row
        for (int f = 0; f < n; f++) {
            // Getting Cofactor of mat[0][f]
            getCofactor(mat, temp, 0, f, n);
            D += sign * mat[0][f] * determinantOfMatrix(temp, n - 1);

            // terms are to be added with alternate sign
            sign = -sign;
        }

        return D;
    }
}