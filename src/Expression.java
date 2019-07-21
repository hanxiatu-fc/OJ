import java.util.Stack;

public class Expression {

    public static void main(String[] args) {
        cal("1+2");
        cal("1+2*3");
        cal("1+2*3/4");
        cal("1+2*3/4*5");
        cal("1+(2+3)");
        cal("1*(2+3)");
        cal("11+(2+3*3)/2+4*2+2*(1+42)*2/3");
        cal("321");
        cal("MAX(11+(2+3*3)/2+4*2+2*(1+42)*2/3,3)");
        cal("MIN(2,3)");
        cal("MAX(MIN(2,3)*2,MAX(4,MIN(5,6)+4/2*(1+2)))");

        String ee = "MAX(MIN(2,3)*2,MAX(4,MIN(5,6)+4/2*(1+2)))";
        String exp = "1+(ee*2/(4-2)*3+3*ee/2+ee+ee*(1+(2+1)/2))*2";
        exp = exp.replaceAll("ee", ee);
        cal(exp);

        cal("MAX(MAX(4,MIN(5,6)),MIN(14,MAX(2,MIN(4,9))))");

    }

    private static void cal(String exp) {
        String tmpExp = exp;

        tmpExp = tmpExp.replaceAll("MAX\\(", MAX).replaceAll("MIN\\(", MIN);

        System.out.println(look(tmpExp));
    }

    private static int look(String exp) {
        String tmpMax = lookMaxOrMin(exp, MAX);
        String tmp = lookMaxOrMin(tmpMax, MIN);

        return lookNormal(tmp);
    }

    private static String lookMaxOrMin(String exp, String op) {
        String tmpExp = exp;

        while (tmpExp.contains(op)) {
            int size = tmpExp.length();
            int indexOp = tmpExp.indexOf(op);
            int indexComma = -1;
            int indexBracket = -1;

            int i = indexOp + 1;
            int count = 0;
            while (i < size) {
                char cur = tmpExp.charAt(i);
                if(cur == 'A' || cur == 'I') {
                    count ++;
                } else if(cur == ',') {
                    if(count == 0) {
                        indexComma = i;
                        break;
                    } else {
                        count --;
                    }
                }
                i ++;
            }

            String aExp = tmpExp.substring(indexOp + 1, indexComma);
            int aResult = look(aExp);

            i = indexComma + 1;
            count = 0;
            while (i < size) {
                char cur = tmpExp.charAt(i);
                if(cur == 'A' || cur == 'I' || cur == '(') {
                    count ++;
                } else if(cur == ')') {
                    if(count == 0) {
                        indexBracket = i;
                        break;
                    } else {
                        count --;
                    }
                }
                i ++;
            }

            String bExp = tmpExp.substring(indexComma + 1, indexBracket);
            int bResult = look(bExp);

            int result = cal(aResult, bResult, op.charAt(0));

            tmpExp = replaceIndex(indexOp, indexBracket, tmpExp, String.valueOf(result));
        }

        return tmpExp;
    }

    private static int lookNormal(String exp) {

        Stack<Character> opStack = new Stack<>();
        Stack<Integer> dataStack = new Stack<>();

        Stack<Character> expStack = new Stack<>();
        char[] expArray = exp.toCharArray();
        int size = expArray.length;

        for(int i = size - 1; i >= 0; i --) {
            expStack.push(expArray[i]);
        }

        StringBuffer sb = new StringBuffer();
        while (!expStack.isEmpty()) {
            char cur = expStack.pop();
            boolean isNum = isNum(cur);
            if(isNum) {
                sb.append(cur);
                if(expStack.isEmpty()) { // end with num
                    addData(sb, dataStack, opStack);
                    sb = new StringBuffer();
                }
                continue;
            } else {
                addData(sb, dataStack, opStack);
                sb = new StringBuffer();
            }

            opStack.push(cur);

            if(cur == ')') {
                opStack.pop();// remove ')'
                while (opStack.peek() != '(') {
                    char op = opStack.pop();
                    int b = dataStack.pop();
                    int a = dataStack.pop();
                    dataStack.push(cal(a, b, op));
                }
                opStack.pop(); // remove '('
                if(opStack.peek() == '*' || opStack.peek() == '/') {
                    char op = opStack.pop();
                    int b = dataStack.pop();
                    int a = dataStack.pop();
                    dataStack.push(cal(a, b, op));
                }
            }
        }

        while (!opStack.isEmpty()) {
            char op = opStack.pop();
            int b = dataStack.pop();
            int a = dataStack.pop();
            dataStack.push(cal(a, b, op));
        }

        return dataStack.peek();
    }

    private static void addData(StringBuffer sb, Stack<Integer> dataStack, Stack<Character> opStack) {
        String data = sb.toString();
        if(!data.isEmpty()) {
            dataStack.push(Integer.parseInt(data));

            if(!opStack.isEmpty()) {
                if(opStack.peek() == '*' || opStack.peek() == '/') {
                    char op = opStack.pop();
                    int b = dataStack.pop();
                    int a = dataStack.pop();
                    dataStack.push(cal(a, b, op));
                }
            }
        }
    }


    private static final String MAX = "A";
    private static final String MIN = "I";

    private static int cal(int a, int b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            case 'A':
                return Math.max(a, b);
            case 'I':
                return Math.min(a, b);
            default:
                throw new RuntimeException("Invalid op : " + op);
        }
    }

    private static boolean isNum(char c) {
        return c >= '0' && c <= '9';
    }

    private static String replaceIndex(int from, int to, String src,String replacement){
        return src.substring(0, from) + replacement + src.substring(to + 1);
    }
}
