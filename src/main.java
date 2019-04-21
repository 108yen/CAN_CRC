import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

//ID:0x19A	000110011010000000100000001

public class main {

	public static void main(String[] args) throws IOException {
		int[] bin = null;
		String str;
		Scanner scan = new Scanner(System.in);

		do {
			System.out.println("\n1:送信データ系列の生成　2:受信データ系列の誤り検出　3:同じCRCの送信データ系列生成　4:終了");
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

	private static void hammingDistance(int[] bin) throws IOException {
		int dis=0;
		String line;
		int[] genBin = new int[bin.length+15];
		File file = new File("binList.txt");
		FileReader filereader = new FileReader(file);
		BufferedReader in = new BufferedReader(filereader);
		file = new File("lessHamming.txt");
		FileWriter filewriter = new FileWriter(file);

		filewriter.write("入力ビット列：");
		for(int m : bin) {
			filewriter.write(String.valueOf(m));
		}
		filewriter.write("\n\n");

		for(int i=0;i<3;i++) {
			in.readLine();
		}
		while((line=in.readLine()) != null) {
			dis=0;
			for(int i=0;i<genBin.length;i++) {
				genBin[i]=Character.getNumericValue(line.charAt(i));
			}
			for(int i=0;i<bin.length;i++) {
				if(genBin[i]!=bin[i]) {
					dis++;
				}
			}
			if(dis<8) {
				System.out.print("hamming_distance："+dis+"  ");
				filewriter.write("hamming_distance："+dis+"  ");
				for(int i=0;i<genBin.length-15;i++) {
					System.out.print(genBin[i]);
					if(i==1 || i==12 || i==13 || i==19) {
						filewriter.write(" ");
					}
					filewriter.write(String.valueOf(genBin[i]));
				}
				System.out.println();
				filewriter.write("\n");
			}
		}
		filewriter.close();

	}

	private static void detectBin(int[] bin) throws IOException {
		int[] CRC = genCRC(bin);
		int[] genBin = new int[bin.length+15];
		int[] sBin = new int[bin.length];
		int n=0;
		int pattern=0;
		Random rnd = new Random();
		File file = new File("binList.txt");
		FileWriter filewriter = new FileWriter(file);

		Arrays.fill(sBin, 0);

		filewriter.write("入力ビット列：");
		for(int m : bin) {
			filewriter.write(String.valueOf(m));
		}
		filewriter.write("\nCRC：");
		for(int m : CRC) {
			filewriter.write(String.valueOf(m));
		}
		filewriter.write("\n\n");

		pattern=(int) Math.pow(2, bin.length-15);
//		pattern=100;

		for(int j=0;j<pattern;j++) {
			Arrays.fill(genBin, 0);

			System.out.print("入力ビット列：");
			for(int m : sBin) {
				System.out.print(m);
			}
			System.out.print("  ");

			for(int i=0;i<genBin.length-15;i++) {

				if(i<15) {
					if(genBin[14]==CRC[14-i]) {
						n=0;
					}else {
						n=1;
					}
				}else {
					if(sBin[i]==0) {
//					if(rnd.nextBoolean()) {
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

				for(int m=0;m<15+i;m++) {
					genBin[15+i-m]=genBin[14+i-m];
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
//				filewriter.write(m);
				filewriter.write(String.valueOf(m));
			}
			System.out.println();
			filewriter.write("\n");

			sBin=incBin(sBin);
		}
		filewriter.close();

		hammingDistance(bin);
	}

	private static int[] incBin(int[] bin) {
		boolean inc = false;

		for(int i=0;i<bin.length;i++) {
			if(i==0) {
				inc = bin[bin.length-1]==1 ? true:false;
				bin[bin.length-1]=bin[bin.length-1]^1;
			}else {
				if(inc) {
				inc = bin[bin.length-1-i]==1 ? true:false;
				bin[bin.length-1-i]=bin[bin.length-1-i]^1;
				}else {
					inc = false;
				}
			}
		}

		return bin;
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
