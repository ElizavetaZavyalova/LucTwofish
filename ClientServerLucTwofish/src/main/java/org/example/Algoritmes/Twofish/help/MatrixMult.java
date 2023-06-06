package org.example.Algoritmes.Twofish.help;


public record MatrixMult(){

        public  static int makeMult(byte[][]matrix, long vector,byte mod){
            int result=0;
            for(int i=0; i< matrix.length;i++){
                byte vectorElem=0;
                for(int j=0; j<matrix[0].length;j++){
                    byte val=(byte)(((0xFF<<((matrix[0].length-1-j)<<3))&vector)>>((matrix[0].length-1-j)<<3));
                    vectorElem^=(GF.multiplication(matrix[i][j],val,mod));
                }
                result|= (long) (vectorElem & 0xFF) <<((matrix.length-1-i)<<3);
            }
            return result;
        }
}
