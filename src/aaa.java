import java.util.Scanner;

public class aaa {
	static private int count = 0;
	static private int min = Integer.MAX_VALUE;
	static private int[][][] M;
	static private int which = 0;

	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		int T = in.nextInt();
		int[] m = new int[T];
		int[] n = new int[T];
		M = new int[T][][];

		for (int t = 0; t < T; t++) {
			m[t] = in.nextInt();
			n[t] = in.nextInt();

			if (m[t] <= n[t]) {
				M[t] = new int[m[t]][n[t]];
				for (int i = 0; i < m[t]; i++) {
					for (int j = 0; j < n[t]; j++) {
						M[t][i][j] = in.nextInt();
					}
				}
			} else {
				int temp = m[t];
				m[t] = n[t];
				n[t] = temp;
				M[t] = new int[m[t]][n[t]];

				for (int j = 0; j < n[t]; j++) {
					for (int i = 0; i < m[t]; i++) {

						M[t][i][j] = in.nextInt();
						// System.out.println(M[t][i][j]);
					}
				}
			}

		}

		for (int t = 0; t < T; t++) {
			int[] queenList = new int[n[t]];
			count = 0;
			min = Integer.MAX_VALUE;
			which = t;
			PlaceQueue(queenList, 1, m[t], n[t]);
			//System.out.println("count=" + count);
			System.out.println("Case " + (which + 1) + ": " + min);
		}
/*
		for (int t = 0; t < T; t++) {

			for (int i = 0; i < m[t]; i++) {
				for (int j = 0; j < n[t]; j++) {

					System.out.print(M[t][i][j] + " ");
				}
				System.out.println();
			}

		}
		*/

	}

	static public void PlaceQueue(int[] queenList, int col, int m, int n) {
		int row = 0;

		if (col == n) // 结束标志
		{
			if (IsSafe_col(queenList, m)) {
				count++;
				int temp = 0;
				for (int i = 0; i < n; i++) {
					//System.out.print(queenList[i] + " ");
					temp += M[which][queenList[i]][i];

				}
				if (temp < min) {
					min = temp;
				}
				//System.out.println();
			}

			// 当处理完第8列的完成

		} else {
			while (row < m) {

				queenList[col] = row;
				// 找下一列的安全位置
				PlaceQueue(queenList, col + 1, m, n);

				row++;

			}
		}

	}

	static boolean IsSafe_col(int[] queenList, int row) {
		int[] fill = new int[row];
		for (int i = 0; i < row; i++) {
			fill[i] = 0;
		}
		for (int i = 0; i < queenList.length; i++) {
			fill[queenList[i]] = 1;
		}
		for (int i = 0; i < row; i++) {
			if (fill[i] == 0) {
				return false;
			}
		}
		return true;
	}
}
