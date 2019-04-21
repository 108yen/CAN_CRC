import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		int[] bin = null;
		String str;
		Scanner scan = new Scanner(System.in);

		do {
			System.out.println("1:送信データ系列の生成　2:受信データ系列の誤り検出　3:同じCRCの送信データ系列生成　4:終了");
			str = scan.nextLine();

			if(!(str.equals("4"))) {
				bin = getBin();
			}

			if(str.equals("1")) {
				genCRC(bin);
			}else if(str.equals("2")) {
				checkCRC(bin);
			}else if(str.equals("3")) {
				detectBin(bin);
			}
		}while(!(str.equals("4")));
	}

	private static void detectBin(int[] bin) {
		int[] CRC = genCRC(bin);
		int[] genBin = new int[bin.length+15];
		int n=0;
		Random rnd = new Random();

		Arrays.fill(genBin, 0);

		for(int i=0;i<genBin.length-15;i++) {

			if(i<15) {
				if(genBin[14]==CRC[14-i]) {
					n=0;
				}else {
					n=1;
				}
			}else {
				if(rnd.nextBoolean()) {
					n=0;
				}else {
					n=1;
				}
			}

			genBin[0]=genBin[0]^n;
			genBin[4]=genBin[4]^n;
			genBin[6]=genBin[6]^n;
			genBin[7]=genBin[7]^n;
			genBin[10]=genBin[10]^n;
			genBin[11]=genBin[11]^n;
			genBin[14]=genBin[14]^n;

			for(int j=0;j<15+i;j++) {
				genBin[15+i-j]=genBin[14+i-j];
			}
			genBin[0]=n;

//			for(int m : genBin) {
//				System.out.print(m);
//			}
//			System.out.println();
		}

		System.out.print("計算結果:");
		for(int m : genBin) {
			System.out.print(m);
		}
		System.out.println();
		//		for(int n : CRC) {
		//			System.out.print(n);
		//		}
	}

	private static int[] getBin() {
		int[] bin;
		String str;
		Scanner scan = new Scanner(System.in);

		System.out.print("2元データ系列:");
		str = scan.nextLine();
		bin = new int[str.length()];
		Arrays.fill(bin, 0);

		for(int i=0;i<str.length();i++) {
			if(str.charAt(i)=='1') {
				bin[i]=1;
			}else {
				bin[i]=0;
			}
		}

		return bin;
	}

	private static int[] genCRC(int[] getBin) {
		//		CAN-15-CAN:110001011001100
		int[] bin = new int[getBin.length+15];
		Arrays.fill(bin, 0);
		for(int i=0;i<getBin.length;i++) {
			bin[i]=getBin[i];
		}

		for(int i=0;i<bin.length-15;i++) {
			bin[1]=bin[1]^bin[0];
			bin[5]=bin[5]^bin[0];
			bin[7]=bin[7]^bin[0];
			bin[8]=bin[8]^bin[0];
			bin[11]=bin[11]^bin[0];
			bin[12]=bin[12]^bin[0];
			bin[15]=bin[15]^bin[0];

			for(int j=0;j<bin.length-i-1;j++) {
				bin[j]=bin[j+1];
			}
		}
		System.out.print("CRCコード:");
		for(int i=0;i<15;i++) {
			System.out.print(bin[i]);
		}
		System.out.println();

		return Arrays.copyOfRange(bin, 0, 15);
	}

	private static void checkCRC(int[] bin) {
		for(int i=0;i<bin.length-15;i++) {
			bin[1]=bin[1]^bin[0];
			bin[5]=bin[5]^bin[0];
			bin[7]=bin[7]^bin[0];
			bin[8]=bin[8]^bin[0];
			bin[11]=bin[11]^bin[0];
			bin[12]=bin[12]^bin[0];
			bin[15]=bin[15]^bin[0];

			for(int j=0;j<bin.length-i-1;j++) {
				bin[j]=bin[j+1];
			}

//			for(int n : bin) {
//				System.out.print(n);
//			}
//			System.out.println();
		}
		System.out.print("誤り検出コード:");
		for(int i=0;i<15;i++) {
			System.out.print(bin[i]);
		}
		System.out.println();
	}

}
