package my.online.store.spring5webapp;

public class ExpressionTree
{
    static String regex = "\\d+";
    // Root of Binary Tree
    Node root;

    ExpressionTree(){ root = null; }

    /* Given a binary tree, print its nodes in inorder*/
    int caculate(Node node)
    {
        if (node == null)
            return 0;


        if(node.key.matches(regex)){
            return Integer.parseInt(node.key);
        } else{
            if("*".equals(node.key)){
                return caculate(node.left) * caculate(node.right);
            } else if("/".equals(node.key)) {
                return caculate(node.left) / caculate(node.right);
            } else if("+".equals(node.key)) {
                return caculate(node.left) + caculate(node.right);
            } else {
                return caculate(node.left) - caculate(node.right);
            }
        }
    }

    // Wrappers over above recursive functions
    int caculate()    {     return caculate(root);   }

    // Driver method
    public static void main(String[] args)
    {
        //1. test caculation
        ExpressionTree tree = new ExpressionTree();
        tree.root = new Node("*");
        tree.root.left = new Node("+");
        tree.root.right = new Node("-");
        tree.root.left.left = new Node("1");
        tree.root.left.right = new Node("2");
        tree.root.right.left = new Node("4");
        tree.root.right.right = new Node("2");
        System.out.println(tree.caculate());
        assert(6==tree.caculate());

        //2. test put tree
        ExpressionTree myTree = new ExpressionTree();
        myTree.root = PutTree.generateNode("(9+1)*2*3");
        System.out.println(myTree.caculate());
        assert(60==myTree.caculate());
        myTree.root = PutTree.generateNode("(9+1)*(2+3)");
        System.out.println(myTree.caculate());
        assert(50==myTree.caculate());
        myTree.root = PutTree.generateNode("9*3");
        System.out.println(myTree.caculate());
        assert(27==myTree.caculate());
    }
}

class Node
{
    String key; //must not null, can only be an operator of "+-*/" if has left and right, or and integer then both left/right must null!
    Node left, right; //must be a value or a whole expression that will generate a value,

    public Node(String item)
    {
        if(item==null){
            throw new RuntimeException("key can not null!"+item);
        }
        if(!item.matches(ExpressionTree.regex) && ("+-x*".indexOf(item)==-1 || item.length()>1)){
            throw new RuntimeException("key not right!"+item);
        }
        key = item;
        left = right = null;
    }

    public Node(String key, Node left, Node right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }
}

class PutTree{
    String left;
    String right;
    String operator;

    static Node generateNode(String expr){
        PutTree pt = new PutTree(expr);
        if(pt.operator.matches(ExpressionTree.regex)){
            return new Node(pt.operator);
        }else{
            return new Node(pt.operator, generateNode(pt.left), generateNode(pt.right));
        }
    }

    public PutTree(String expr) {

        expr = expr.trim();
        if(expr.startsWith("(")){
            int index = getRightBracket(expr.substring(1));
            left = expr.substring(1, index+1);  //(12+3)
            operator = expr.substring(index+1+1, index+1+2);
            right = expr.substring(index+1+2);  //(12+3)
        }else{
            boolean onlyDigit = false;
            int add = expr.indexOf("+");
            int minus = expr.indexOf("-");
            int index = -1;
            if(add!=-1){
                index = add;
            }
            if(minus!=-1){
                index = index==-1 ? minus : Math.min(index, minus);
            }
            if(index == -1){
                //no +- only */, then get first /
                index =  expr.indexOf("/");
                if(index == -1){
                    index =  expr.indexOf("*");
                }
                if(index == -1){
                    //no +-*/ must a digit
                    onlyDigit = true;
                }
            }
            if(onlyDigit){
                left = null;
                operator = expr;
                right = null;
            }else{
                left = expr.substring(0, index);  //(12+3)
                operator = expr.substring(index, index+1);
                right = expr.substring(index+1);  //(12+3)
            }
        }
        left = removeOutBracket(left);
        right = removeOutBracket(right);
        operator = removeOutBracket(operator);
    }

    String removeOutBracket(String ss){
        if(ss==null){
            return null;
        }
        String s = ss.trim();
        if(s.startsWith("(")&&s.endsWith(")")){
            return s.substring(1, s.length()-1);
        }
        return s;
    }

    int getRightBracket(String sub){
        int lever = 0;
        for(int i=0; i<sub.length(); i++){
            if(sub.substring(i,i+1).equals("(")){
                lever += 1;
            }
            if(sub.substring(i,i+1).equals(")")){
                if(lever==0){
                    return i;
                }else{
                    lever -= 1;
                }
            }
        }
        throw new RuntimeException("no ) match!");
    }

    @Override
    public String toString() {
        return "PutTree{" +
                "left='" + left + '\'' +
                ", right='" + right + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }
}