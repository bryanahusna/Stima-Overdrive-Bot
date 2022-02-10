package za.co.entelect.challenge.algorithm;

import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.utils.Tree;

import java.util.LinkedList;
import java.util.Queue;


public class Search {
    public Tree tree;
    public Search(){
        tree = new Tree(new GameState());
        Queue<Tree.Node> q = new LinkedList<>();
        q.add(tree.Root);
        while(!q.isEmpty()){
            Tree.Node curNode = q.remove();
            for(Tree.Node node: )
        }
    }
}
