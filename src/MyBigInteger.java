public class MyBigInteger implements Comparable<MyBigInteger>{

    private int[] digits;
    private String stringNumber;
    private int numberOfBits;

    public MyBigInteger(int numberOfBits){
        this.numberOfBits = numberOfBits;
        digits = new int[numberOfBits];
    }

    public void setValue(String value){
        stringNumber = value;

        for(int i = value.length() - 1, j = numberOfBits - 1; i>= 0; i--, j--){
            digits[j] = Character.getNumericValue(value.charAt(i));
        }
    }

    public int[] getArrayValue(){
        return  digits;
    }

    public int digitAt(int position){
        return digits[position];
    }

    public int getNumberOfBits(){
        return  numberOfBits;
    }

    public String getStringValue(){
        StringBuilder stringBuilder = new StringBuilder();

        int i = findBegining();

        for(int j = i; j<numberOfBits; j++){
            stringBuilder.append(Integer.toString(digits[j]));
        }

        return stringBuilder.toString();
    }

    public void add(MyBigInteger number){
        for(int i = numberOfBits - 1, j = number.getNumberOfBits() - 1; j>= 0; i--, j--){
            digits[i] += number.digitAt(j);
            if(digits[i] > 9){
                digits[i] = digits[i] % 10;
                digits[i - 1] += 1;
            }
        }
    }

    public void divide(MyBigInteger number){
       // int i = findBegining();
        int x = 0;
        int result[] = new int[numberOfBits];

      //  for(int j = i; j<numberOfBits; i++){
         //   int current = 0;
          //  int reminder = 0;
          //  if(this.compareTo(number) == -1){
           //     current = digits[j] * 10 + digits[j + 1];
          //      j += 2;
          //  }
          //  result[x] = current / 5;
          //  reminder = current - (result[x] * 5);
      //  }



        String numberOne = this.getStringValue();
        String numberTwo = number.getStringValue();
        String s;

        while(numberOne.length() < numberTwo.length()){
            numberOne = '0' + numberOne;
        }
        while (numberTwo.length() > numberOne.length()){
            numberTwo = '0' + numberTwo;
        }
        int one = numberOne.charAt(0) - '0';
        int two = numberTwo.charAt(0) - '0';
        int diff = 0;

        if(one < two){
            System.out.println("Quotient: 0 " + "reminder " + one);
        }
        else{
            numberOne = '0' + numberOne;
            numberTwo = '0' + numberTwo;
            diff = two;
        }

      do{
          for(int i = numberOne.length() - 1; i>= 0; i++){
              one = numberOne.charAt(i) - '0';
              two = numberTwo.charAt(i) - '0';

              //if(one < two){
                 // numberOne.charAt(i - 1) =


              //}

          }
      }while(true);
    }

    public int findBegining()
    {
        int i = 0;
        do{
            i++;
        }while(digits[i] == 0);

        return i;
    }
    @Override
    public int compareTo(MyBigInteger o)
    {
       if((numberOfBits - findBegining()) > (o.numberOfBits - o.findBegining())){
           return  1;
       }
       else if((numberOfBits - findBegining()) < (o.numberOfBits - o.findBegining())){
           return  -1;
       }
       else if((numberOfBits - findBegining()) == (o.numberOfBits - o.findBegining())){

           boolean comparsion = false;
           int i = findBegining();
           int j = o.findBegining();
           do{
               comparsion = (digits[i] == o.digits[j]);
               i++;
               j++;
           }while(comparsion == true && i != getNumberOfBits() && j != o.getNumberOfBits());

           if(digits[i - 1] > o.digits[j - 1]){
               return 1;
           }else if(digits[i - 1] < o.digits[j - 1]){
               return -1;
           }
       }
       return  0;
    }

}
