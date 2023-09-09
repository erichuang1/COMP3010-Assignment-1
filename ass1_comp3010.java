// ass1- COMP3010 - S2 2023
// author: Bernard mans
// Cluster Load Balancing
// Skeleton program for input and output

import java.io.*;
import java.util.Scanner;

public class ass1_comp3010 {

    public static void main(String[] args) {

        int proc, // number of processors
                rows, // number of rows in the matrix of the loads
                cols; // number of columns in the matrix of the loads

        int r, c, p; // index for manipulating arrays

        Scanner keyboard = new Scanner(System.in);

        System.out.println("Enter the number of processors that need to share the load:  ");
        proc = keyboard.nextInt();

        if (proc < 2) {
            System.out.println("Too few processors");
            System.exit(0);
        }

        System.out.println("Enter the number of rows of the matrix of the loads: ");
        rows = keyboard.nextInt();
        System.out.println("Enter the number of columns of the matrix of the loads: ");
        cols = keyboard.nextInt();

        if ((rows <= proc) || (cols <= proc)) {
            System.out.println("Matrix too small");
            System.exit(0);
        }

        int[][] M = new int[rows][cols]; // create Matrix of loads

        System.out.println("Enter the load matrix (row by row, left to right): ");

        for (r = 0; r < rows; r++) {
            for (c = 0; c < cols; c++) {
                M[r][c] = keyboard.nextInt();
            }
        }

        // initialising variables for results
        int maxLoad = 0; // Maximum load of all regions
        int[][] C = new int[4][proc]; // Result Corner matrix

        // ============ DO SOMETHING HERE TO SOLVE THE PROBLEM ============
        // @complexity0 | @complexity1 + 2*@complexity4
        // @complexity0 | n + 2*(prоc * n * ((rоws+1)/prоc + cоls*(2*cоls + 1) + 2) +
        // 4*prоc)c * 4n*(cоls + 1)
        // @complexity0 | O(n + 2*prоc*n * (rоws/prоc + 2*cоls^2) + 8*prоc)roc*cоls)
        // @complexity0 | n + 2*prоc*n * (rоws/prоc + 2n) + 8*prоccоls))
        // @complexity0 | n + 2*prоc*n*rоws/prоc + 2*prоc*n*2n + 8*prоc
        // @complexity0 | n + 2n*rоws + 4n^2*prоc + 8*prоc
        // FINAL COMPLEXITY = O(n^2*proc)

        // calculate some basic stats
        // @complexity1 | n
        int sumLoad = 0;
        for (r = 0; r < rows; r++) {
            for (c = 0; c < cols; c++) {
                sumLoad += M[r][c];
                // maxLoad = Math.max(maxLoad, M[r][c]);
            }
        }
        // float avgLoad = sumLoad / (float) proc;
        // System.out.println("sumLoad=" + sumLoad + "\n"
        // + "maxLoad=" + maxLoad + "\n"
        // + "avgLoad=" + avgLoad);

        int[][] d = new int[proc][4]; // helper result matrix
        // try partitioning different ways
        // @complexity4 | prоc * (O(n/prоc) + [cоls * cоls] + O(n/prоc) + [O(cоls) +
        // O(n)]) + proc + 4*prоc
        Result rrows = tryRows(proc, rows, cols, new Matrix(M, false), sumLoad);
        // @complexity2 | prоc * (O(n/prоc) + [rows * rows] + O(n/prоc) + [O(rows) +
        // O(n)]) + proc + 4*prоc
        Result rcols = tryRows(proc, cols, rows, new Matrix(M, true), sumLoad);
        if (rrows.maxLoad <= rcols.maxLoad) {
            d = rrows.d;
            maxLoad = rrows.maxLoad;
        } else {
            d = rcols.d;
            maxLoad = rcols.maxLoad;
        }

        C = d;

        // ======================== END OF WORK ========================

        // output
        System.out.println();
        System.out.println("The maximum load (first line) followed by ");
        System.out.println("the " + proc + " regions selected are (top row, left column, bottom row, right column): ");
        System.out.println(maxLoad);
        for (p = 0; p < proc; p++) {
            System.out.println(C[0][p] + " " + C[1][p] + " " + C[2][p] + " " + C[3][p]);
        }
    }

    // @complexity4 | @complexity5 + @complexity16 + @complexity17
    // @complexity4 | prоc * (O(n/prоc) + [cоls * cоls] + O(n/prоc) + [O(cоls) +
    // O(n)]) + proc + 4*prоc
    private static Result tryRows(int proc, int rows, int cols, Matrix M, int sumLoad) {
        int r, c, p; // helper variables
        int[][] d = new int[proc][4]; // helper result matrix

        // for each processor, assign its own rows-region
        boolean procAssigned = false;
        boolean terminate = false;
        int maxSum = 0; // the largest rows-region sums between all processors
        r = 0;
        int row_start = 0;
        int avgLoad = sumLoad / proc;
        // @complexity5 | prоc * (@complexity6 + @complexity8)
        // @complexity5 | prоc * (O(n/prоc) + [cоls * cоls] + O(n/prоc) + [O(cоls) +
        // O(n)])
        for (p = 0; p < proc;) {
            procAssigned = false;
            // d[p] = new int[] { row_start, 0, 0, 0 };
            int sum = 0;

            // sum up everything (sumLoad)
            // calculate average load for each processor (avgLoad)

            // ending check, check if this proc is the last processor
            // @complexity6 | O(n/prоc)
            if (p == proc - 1) {
                sum = 0;
                // if so, finish everything and terminates
                // sum everything (all rows) left
                // @complexity7 | O(n/prоc)
                for (r = row_start; r < rows; r++)
                    for (c = 0; c < cols; c++)
                        sum += M.getMatrix(r, c);
                // record maxSum (see def var for what it does)
                maxSum = Math.max(maxSum, sum);

                // the actual return/submission
                d[p] = new int[] { row_start, 0, rows - 1, cols - 1 };
                // terminators
                terminate = true;
            }

            // terminate check
            if (terminate)
                break;

            // sum up rows until exceeding avgLoad
            // @complexity8 | [cоls * cоls] + O(n/prоc) + [O(cоls) + O(n)]
            for (r = row_start; r < rows;) {
                // @complexity9 | cоls * cоls
                for (c = 0; c < cols; c++) {
                    sum += M.getMatrix(r, c);
                    // assign condition: exceeding avgLoad
                    if (sum >= avgLoad) {
                        // check whether retracting or promoting cols is closer to avgLoad
                        int c1, c2;
                        // retracting cols
                        // @complexity10 | cоls/2
                        int sum1 = sum;
                        for (c1 = c + 1; c1 > 0; c1--)
                            sum1 -= M.getMatrix(r, c1 - 1);
                        // promoting cols
                        // @complexity11 | cоls/2
                        int sum2 = sum;
                        for (c2 = c + 1; c2 < cols; c2++)
                            sum2 += M.getMatrix(r, c2);
                        // check difference
                        if (Math.abs(sum1 - avgLoad) <= Math.abs(sum2 - avgLoad)) {
                            // sum1 is better, return sum1
                            r = r - 1;
                            c = c1;
                            sum = sum1;
                        } else {// sum2 is better, return sum2
                            // r = r; // (no change)
                            c = c2;
                            sum = sum2;
                        }

                        // the actual return/submission
                        d[p] = new int[] { row_start, 0, r, cols - 1 };
                        row_start = r + 1;
                        // avgLoad = ((proc - p) * avgLoad - sum2) / (proc - p - 1);
                        sumLoad = sumLoad - sum;
                        avgLoad = sumLoad / (proc - p - 1);
                        p++;
                        // record maxSum (see def var for what it does)
                        maxSum = Math.max(maxSum, sum);
                        // exit for
                        procAssigned = true;
                    }
                    // exit for
                    if (procAssigned)
                        break;
                }
                r++;

                // ending check, check if this proc is the last processor
                // @complexity12 | O(n/prоc)
                if (p == proc - 1) {
                    sum = 0;
                    // if so, finish everything and terminates
                    // sum everything (all rows) left
                    // @complexity13 | O(n/prоc)
                    for (r = row_start; r < rows; r++)
                        for (c = 0; c < cols; c++)
                            sum += M.getMatrix(r, c);
                    // record maxSum (see def var for what it does)
                    maxSum = Math.max(maxSum, sum);

                    // the actual return/submission
                    d[p] = new int[] { row_start, 0, rows - 1, cols - 1 };
                    // terminators
                    terminate = true;
                }

                // terminate check
                if (terminate)
                    break;

                // remaining proc check
                // if remain processors == remain rows, finish everything and terminate
                // @complexity13 | O(cоls) + O(n)
                if (proc - p - 1 == rows - r) {
                    if (procAssigned) {
                        sum = 0;
                        p++;
                    } else { // if not yet assigned, force assign the current processor
                        r--;
                        // sum everything left on this row
                        // @complexity14 | O(cоls)
                        for (c = 0; c < cols; c++)
                            sum += M.getMatrix(r, c);
                        d[p] = new int[] { row_start, 0, r, cols - 1 };
                        row_start = r + 1;
                        // record maxSum (see def var for what it does)
                        maxSum = Math.max(maxSum, sum);
                        procAssigned = true;
                        p++;
                    }
                    // sum up the remaining items for each row
                    // @complexity15 | O(n)
                    for (r = row_start; r < rows; r++) {
                        sum = 0;
                        for (c = 0; c < cols; c++)
                            sum += M.getMatrix(r, c);
                        // assign each row to each processor
                        d[p] = new int[] { r, 0, r, cols - 1 };
                        // record maxSum (see def var for what it does)
                        maxSum = Math.max(maxSum, sum);
                        p++;
                    }
                    // exit for
                    terminate = true;
                }

                // terminate check
                if (procAssigned || terminate)
                    break;
            }

            // terminate check
            if (terminate)
                break;
        }

        // swap rows and cols for the result matrix
        // if the input matrix is set to be transposed
        // (since the input matrix was interprated as fliped during the process)
        // @complexity16 | prоc
        if (M.transposed) {
            for (int i = 0; i < d.length; i++) {
                int tmp;
                tmp = d[i][0];
                d[i][0] = d[i][1];
                d[i][1] = tmp;
                tmp = d[i][2];
                d[i][2] = d[i][3];
                d[i][3] = tmp;
            }
        }
        Matrix d2 = new Matrix(d, true);
        // @complexity17 | 4*prоc (for exportMatrix)
        return new Result(d2.exportMatrix(), maxSum);
    }
}