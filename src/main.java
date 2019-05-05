import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
			switch (str) {
			case "1":
				System.out.println("CRCコード:");
				for(int i : genCRC(bin)) {
					System.out.print(i);
				}
				System.out.println();
				break;
			case "2":
				checkCRC(bin);
				break;
			case "3":
				detectBin(bin);
				break;

			default:
				break;
			}
		}while(!(str.equals("4")));
	}

	private static void hammingDistance(int[] bin) throws IOException {
		int dis=0,row=0,pattern=0;
		String line;
		int[] synth = new int[bin.length+15];
		int[] genBin;
		int[] CRC = genCRC(bin);
		int[] stuffedBin;
		File file = new File("binList.txt");
		FileReader filereader = new FileReader(file);
		BufferedReader in = new BufferedReader(filereader);
		file = new File("lessHamming.txt");
		FileWriter filewriter = new FileWriter(file);
		
		for(int i=0;i<5;i++) {
			pattern += (int) Math.pow(2, bin.length-2+i);
		}

		System.arraycopy(bin,0,synth,0,bin.length);
		System.arraycopy(CRC,0,synth,bin.length,CRC.length);
		stuffedBin=bitStuffing(synth);
		genBin = new int[stuffedBin.length];

		filewriter.write("入力ビット列：       ");
		for(int n : stuffedBin) {
			filewriter.write(String.valueOf(n));
		}
		filewriter.write("\n\n");
		
		System.out.println("\nハミング距離の計算\n");

		for(int i=0;i<3;i++) {
			in.readLine();
		}
		while((line=in.readLine()) != null) {
			if(genBin.length==line.length()) {
				dis=0;
				for(int i=0;i<genBin.length;i++) {
					genBin[i]=Character.getNumericValue(line.charAt(i));
				}
				for(int i=0;i<genBin.length;i++) {
					if(genBin[i]!=stuffedBin[i]) {
						dis++;
					}
				}
				if(dis<8) {
					System.out.print("hamming_distance："+dis+"  ");
					filewriter.write("hamming_distance："+dis+"  ");
					for(int i=0;i<genBin.length;i++) {
						System.out.print(genBin[i]);
						/*if(i==1 || i==12 || i==13 || i==19) {
							filewriter.write(" ");
						}*/
						filewriter.write(String.valueOf(genBin[i]));
					}
					System.out.println();
					filewriter.write("\n");
				}
			}
//			if(++row%1000000==0) {
//				System.out.print((int)((float)row/(float)pattern*100)+"%");
//			}
		}
		filewriter.close();
	}

	private static int[] bitStuffing(int[] bin) {
		int counter=0;
		int[] out;
		List<Integer> result = new ArrayList<Integer>();

		result.add(bin[0]);
		for(int i=1;i<bin.length;i++) {
			if(counter==4) {
				counter=0;
				result.add(bin[i-1]^1);
			}else if(bin[i]==bin[i-1]) {
				counter++;
			}else {
				counter=0;
			}
			result.add(bin[i]);
		}

		out=new int[result.size()];
		for(int i=0;i<result.size();i++) {
			out[i]=result.get(i);
		}

		return out;
	}

	private static void detectBin(int[] bin) throws IOException {
		int[] CRC = genCRC(bin);
		int[] inputBin = new int[bin.length];	//逆回路に入力するビット列
		int pattern=0;
		String mode;
		Scanner scan = new Scanner(System.in);
		Random rnd = new Random();
		File file = new File("binList.txt");
		FileWriter filewriter = new FileWriter(file);

		//		File Header
		filewriter.write("入力ビット列：");
		for(int m : bin) {
			filewriter.write(String.valueOf(m));
		}
		filewriter.write("\nCRC：");
		for(int m : CRC) {
			filewriter.write(String.valueOf(m));
		}
		filewriter.write("\n\n");

		//		generate bit strings
		/*
				入力bit長の前後＋－２の長さのbit列生成
		 */
		System.out.println("\n1:CRCから逆算 2:全てのビット列を生成");
		mode=scan.nextLine();

		for(int i=0;i<5;i++) {
			switch (mode) {
			case "1":
				for(int[] n : inversionBin(CRC, (bin.length-2+i))) {
					for(int m : bitStuffing(n)) {
						filewriter.write(String.valueOf(m));
					}
					filewriter.write("\n");
				}
				break;
			case "2":
				genAllBin(filewriter, bin.length-2+i);
				break;
			default:
				break;
			}

		}
		filewriter.close();

		hammingDistance(bin);
	}

	private static void genAllBin(FileWriter filewriter, int binLength) throws IOException{
		int pattern=(int) Math.pow(2, binLength);
		int[] genBin = new int[binLength+15];
		int[] inputBin = new int[binLength];
		int n = 0;
		
		System.out.println(binLength+"ビットのビット列生成");
		Arrays.fill(inputBin, 0);

		for(int i=0;i<pattern;i++) {
			Arrays.fill(genBin, 0);
			for(int j=0;j<binLength;j++) {
				if(inputBin[j]==0) {
					n=0;
				}else {
					n=1;
				}

				genBin[0]=genBin[0]^n;
				genBin[4]=genBin[4]^n;
				genBin[6]=genBin[6]^n;
				genBin[7]=genBin[7]^n;
				genBin[10]=genBin[10]^n;
				genBin[11]=genBin[11]^n;
				genBin[14]=genBin[14]^n;

				for(int m=0;m<15+j;m++) {
					genBin[15+j-m]=genBin[14+j-m];
				}
				genBin[0]=n;
			}
			for(int m : bitStuffing(genBin)) {
				filewriter.write(String.valueOf(m));
			}
			filewriter.write("\n");
			
			inputBin=incBin(inputBin);
			
//			if(i%1000000==0) {
//				System.out.println((int)((float)i/(float)pattern*100)+"%");
//			}
		}
	}

	//	CRCに対応したビット列生成
	private static int[][] inversionBin(int[] CRC, int binLength) {
		int pattern=(int) Math.pow(2, binLength-15);
		int[][] genBin = new int[pattern][binLength+15];
		int[] inputBin = new int[binLength];
		int n = 0;

		Arrays.fill(inputBin, 0);

		for(int i=0;i<pattern;i++) {
			Arrays.fill(genBin[i], 0);
			for(int j=0;j<binLength;j++) {

				if(j<15) {
					if(genBin[i][14]==CRC[14-j]) {	//最初の15bitはCRC
						n=0;
					}else {
						n=1;
					}
				}else {
					if(inputBin[j]==0) {
						n=0;
					}else {
						n=1;
					}
				}

				genBin[i][0]=genBin[i][0]^n;
				genBin[i][4]=genBin[i][4]^n;
				genBin[i][6]=genBin[i][6]^n;
				genBin[i][7]=genBin[i][7]^n;
				genBin[i][10]=genBin[i][10]^n;
				genBin[i][11]=genBin[i][11]^n;
				genBin[i][14]=genBin[i][14]^n;

				for(int m=0;m<15+j;m++) {
					genBin[i][15+j-m]=genBin[i][14+j-m];
				}
				genBin[i][0]=n;
			}
			inputBin=incBin(inputBin);
		}

		return genBin;
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
		}
		System.out.print("誤り検出コード:");
		for(int i=0;i<15;i++) {
			System.out.print(bin[i]);
		}
		System.out.println();
	}

}
