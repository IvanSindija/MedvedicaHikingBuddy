package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import android.location.Location;
import android.location.LocationManager;


/**
 * Created by ivan on 19.01.15..
 */

public class KdTree<T extends KdTree.xyPoint> {


    private int k = 2;
    private static final double maxDistance = 50;
    private KdNode root = null;

    private static final Comparator<xyPoint> X_COMPARATOR = new Comparator<xyPoint>() {


        @Override
        public int compare(xyPoint o1, xyPoint o2) {
            if (o1.x < o2.x)
                return -1;
            if (o1.x > o2.x)
                return 1;
            return 0;
        }
    };

    private static final Comparator<xyPoint> Y_COMPARATOR = new Comparator<xyPoint>() {


        @Override
        public int compare(xyPoint o1, xyPoint o2) {
            if (o1.y < o2.y)
                return -1;
            if (o1.y > o2.y)
                return 1;
            return 0;
        }
    };


    protected static final int X_AXIS = 0;
    protected static final int Y_AXIS = 1;


    /**
     * Constructor for creating a more balanced tree. It uses the
     * "median of points" algorithm.
     *
     * @param list
     *            of xyPoints.
     */
    public KdTree(List<xyPoint> list) {
        root = createNode(list, k, 0);
    }

    /**
     * Create node from list of xyPoints.
     *
     * @param list
     *            of xyPoints.
     * @param k
     *            of the tree.
     * @param depth
     *            depth of the node.
     * @return node created.
     */
    private static KdNode createNode(List<xyPoint> list, int k, int depth) {
        if (list == null || list.size() == 0)
            return null;

        int axis = depth % k;
        if (axis == X_AXIS)
            Collections.sort(list, X_COMPARATOR);
        else
            Collections.sort(list, Y_COMPARATOR);

        KdNode node = null;
        if (list.size() > 0) {
            int medianIndex = list.size() / 2;
            node = new KdNode(k, depth, list.get(medianIndex));
            List<xyPoint> less = new ArrayList<xyPoint>(list.size() - 1);
            List<xyPoint> more = new ArrayList<xyPoint>(list.size() - 1);
            // Process list to see where each non-median point lies
            for (int i = 0; i < list.size(); i++) {
                if (i == medianIndex)
                    continue;
                xyPoint p = list.get(i);
                if (KdNode.compareTo(depth, k, p, node.id) <= 0) {
                    less.add(p);
                } else {
                    more.add(p);
                }
            }
            if ((medianIndex - 1) >= 0) {
                // Cannot assume points before the median are less since they
                // could be equal
                // List<xyPoint> less = list.subList(0, mediaIndex);
                if (less.size() > 0) {
                    node.lesser = createNode(less, k, depth + 1);
                    node.lesser.parent = node;
                }
            }
            if ((medianIndex + 1) <= (list.size() - 1)) {
                // Cannot assume points after the median are less since they
                // could be equal
                // List<xyPoint> more = list.subList(mediaIndex + 1,
                // list.size());
                if (more.size() > 0) {
                    node.greater = createNode(more, k, depth + 1);
                    node.greater.parent = node;
                }
            }
        }

        return node;
    }
    /**
     *  Nearest Neighbor search
     *
     * @param value
     *            to find neighbors of.
     * @return collection of T neighbors.
     */
    public boolean nearestNeighbourSearch(T value) {
        if (value == null)
            return false;
        boolean closeEnough=false;
        // Find the closest leaf node
        KdNode prev = null;
        KdNode node = root;
        while (node != null) {
            if (KdNode.compareTo(node.depth, node.k, value, node.id) <= 0) {
                // Lesser
                prev = node;
                node = node.lesser;
            } else {
                // Greater
                prev = node;
                node = node.greater;
            }
        }
        KdNode leaf = prev;
        if (leaf != null) {
            if (leaf.id.distance(value)<maxDistance)
                closeEnough=true;
            // Used to not re-examine nodes
            Set<KdNode> examined = new HashSet<KdNode>();

            // Go up the tree, looking for better solutions
            node = leaf;
            while (node != null && !closeEnough) {
                // Search node
                closeEnough = searchNode(value, node, examined);
                node = node.parent;
            }
        }

        return closeEnough;
    }

    private static final <T extends KdTree.xyPoint> boolean searchNode(T value, KdNode node, Set<KdNode> examined) {
        examined.add(node);

        Double nodeDistance = node.id.distance(value);
        if (nodeDistance < maxDistance)
            return  true;

        int axis = node.depth % node.k;
        KdNode lesser = node.lesser;
        KdNode greater = node.greater;

        // Search children branches, if axis aligned distance is less than
        // current distance
        if (lesser != null && !examined.contains(lesser)) {
            examined.add(lesser);

            double nodePoint;
            double valuePlusDistance;
            if (axis == X_AXIS) {
                nodePoint = node.id.x;
                valuePlusDistance = value.x - maxDistance;
            } else  {
                nodePoint = node.id.y;
                valuePlusDistance = value.y - maxDistance;
            }
            boolean lineIntersectsCube = ((valuePlusDistance <= nodePoint) ? true : false);

            // Continue down lesser branch
            if (lineIntersectsCube)
                return  searchNode(value, lesser, examined);
        }
        if (greater != null && !examined.contains(greater)) {
            examined.add(greater);

            double nodePoint;
            double valuePlusDistance;
            if (axis == X_AXIS) {
                nodePoint = node.id.x;
                valuePlusDistance = value.x + maxDistance;
            } else {
                nodePoint = node.id.y;
                valuePlusDistance = value.y + maxDistance;
            }
            boolean lineIntersectsCube = ((valuePlusDistance >= nodePoint) ? true : false);

            // Continue down greater branch
            if (lineIntersectsCube)
                return searchNode(value, greater,examined);
        }
        return false;
    }


    protected static class distanceComparator implements Comparator<KdNode> {

        private xyPoint point = null;

        public distanceComparator(xyPoint point) {
            this.point = point;
        }

        @Override
        public int compare(KdNode o1, KdNode o2) {
            Double d1 = point.distance(o1.id);
            Double d2 = point.distance(o2.id);
            if (d1.compareTo(d2) < 0)
                return -1;
            else if (d2.compareTo(d1) < 0)
                return 1;
            return o1.id.compareTo(o2.id);
        }
    };

    public static class KdNode implements Comparable<KdNode> {

        private int k = 2;
        private int depth = 0;
        private xyPoint id = null;
        private KdNode parent = null;
        private KdNode lesser = null;
        private KdNode greater = null;

        public KdNode(xyPoint id) {
            this.id = id;
        }

        public KdNode(int k, int depth, xyPoint id) {
            this(id);
            this.k = k;
            this.depth = depth;
        }

        public static int compareTo(int depth, int k, xyPoint o1, xyPoint o2) {
            int axis = depth % k;
            if (axis == X_AXIS)
                return X_COMPARATOR.compare(o1, o2);
            return Y_COMPARATOR.compare(o1, o2);
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof KdNode))
                return false;

            KdNode kdNode = (KdNode) obj;
            if (this.compareTo(kdNode) == 0)
                return true;
            return false;
        }

        @Override
        public int compareTo(KdNode o) {
            return compareTo(depth, k, this.id, o.id);
        }
    }

    public static class xyPoint implements Comparable<xyPoint> {

        protected double x = Double.NEGATIVE_INFINITY;
        protected double y = Double.NEGATIVE_INFINITY;

        public xyPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Computes the  distance from this point to the other.
         *
         * @param o1
         *            other point.
         * @return euclidean distance.
         */
        public double distance(xyPoint o1) {
            return distance(o1, this);
        }

        /**
         * Computes the  distance from one point to the other.
         *
         * @param o1
         *            first point.
         * @param o2
         *            second point.
         * @return  distance.
         */
        private static final double distance(xyPoint o1, xyPoint o2) {
            float [] distance = new float[1];
            Location loc = new Location(LocationManager.NETWORK_PROVIDER);
            loc.distanceBetween(o1.x, o1.y, o2.x, o2.y, distance);
            return distance[0];
        };


        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof xyPoint))
                return false;

            xyPoint xyPoint = (xyPoint) obj;
            return compareTo(xyPoint) == 0;
        }


        @Override
        public int compareTo(xyPoint o) {
            int xComp = X_COMPARATOR.compare(this, o);
            if (xComp != 0)
                return xComp;
            int yComp = Y_COMPARATOR.compare(this, o);
                return yComp;
        }
    }

}
