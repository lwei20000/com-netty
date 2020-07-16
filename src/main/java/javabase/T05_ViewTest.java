package javabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class T05_ViewTest {

    public class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
    }

    class Solution {
        public List<List<Integer>> levelOrder(TreeNode root) {

            //  结果list
            List<List<Integer>> result = new ArrayList<List<Integer>>();

            // 校验二叉树是否为空
            if(root == null) {
                return null;
            }

            // 辅助队列
            LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
            queue.add(root);
            while(queue.size() > 0) {
                int len = queue.size();

                // 临时队列
                List<Integer> tmp = new ArrayList<Integer>();
                for(int i=0; i< len; i++) {
                    TreeNode tn = queue.remove();
                    if(tn.left != null) tmp.add(tn.left.val);
                    if(tn.right != null) tmp.add(tn.right.val);
                }

                result.add(tmp);
            }

            return result;

        }
    }
}
