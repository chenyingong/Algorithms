import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

/**
 * Algorithms 4th, part 2, week 2, assignment 7.
 * @author Chenyin Gong
 *
 */
public class SeamCarver
{
    private Picture p;
    private boolean callHorizontal = false;
    
    /**
     * Create a seam carver object based on the given picture.
     * @param picture picture object
     */
    public SeamCarver(Picture picture)
    {
        if (picture == null)
            throw new IllegalArgumentException("constructor argument is null");
        this.p = new Picture(picture); // deep copy
    }
    
    public Picture picture()
    {
        Picture temp;
        if (!callHorizontal)
        {
            temp = new Picture(p);
            return temp;
        }
        else
        {
            p = transpose(p);
            callHorizontal = false;
            temp = new Picture(p);
            return temp;
        } 
    }
    
    public int width()
    {
        if (!callHorizontal)
            return p.width();
        else return p.height();
    }
    
    public int height()
    {
        if (!callHorizontal)
            return p.height();
        else return p.width();
    }
    
    public double energy(int x, int y)
    {
        if (!callHorizontal)
            return myEnergy(x, y);
        else return myEnergy(y, x);
    }
    
    private double myEnergy(int x, int y)
    {
        if (x < 0 || x >= p.width() || y < 0 || y >= p.height())
            throw new IllegalArgumentException("index outside the prescribed range");
        if (x == 0 || x == p.width() - 1 || y == 0 || y == p.height() - 1)
            return 1000.0;
        else
        {
            Color left = p.get(x-1, y), right  = p.get(x+1, y), 
                    upper  = p.get(x, y-1), bottom = p.get(x, y+1);
            return Math.sqrt(squareGradient(left, right) + 
                    squareGradient(upper, bottom));
        }
    }
    
    private double squareGradient(Color a, Color b)
    {
        double red   = Math.pow(a.getRed() - b.getRed(), 2);
        double green = Math.pow(a.getGreen() - b.getGreen(), 2);
        double blue  = Math.pow(a.getBlue() - b.getBlue(), 2);
        return red + green + blue;
    }
    
    public int[] findHorizontalSeam()
    {
        int[] seam;
        if (!callHorizontal)
        {
            p = transpose(p);
            seam = findSeam();
            callHorizontal = true;
        }
        else seam = findSeam();
        return seam;
    }
      
    public int[] findVerticalSeam()
    {
        int[] seam;
        if (callHorizontal)
        {
            p = transpose(p);
            seam = findSeam();
            callHorizontal = false;
        }
        else seam = findSeam();
        return seam;
        
    }
    
    private int[] findSeam()
    {
        int height = p.height(), width = p.width();
        
        if (height <= 2 || width <= 2)
        {
            int[] seam = new int[height];
            for (int i = 0; i < height; i++)
                seam[i] = 0;
            return seam;
        }
        
        double[] energy = new double[height*width];
        double[] distTo = new double[height*width];
        int[] edgeTo    = new int[height*width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
            {
                energy[trans(i, j)] = myEnergy(j, i); // int[i][j] vertex = i * width + j
                distTo[trans(i, j)] = Double.POSITIVE_INFINITY;
            }
        distTo[0] = 0.0;
        
        for (int v : topological())
            relax(v, distTo, edgeTo, energy);
        
        int[] seam = new int[height];
        for (int i = height - 2, v = width*height-1; i != 0; i--, v = edgeTo[v])
        {
            int j = edgeTo[v] % width;
            seam[i] = j;
        }
        seam[0] = seam[1] - 1;
        seam[height-1] = seam[height-2] - 1;
        return seam;
    }
    
    private Picture transpose(Picture picture)
    {
        int w = picture.height(), h = picture.width();
        Picture pTrans = new Picture(w, h);
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                pTrans.setRGB(j, i, p.getRGB(i, j));
        return pTrans;
    }
    
    private Iterable<Integer> topological()
    {
        // topological algorithm  
        int height = p.height(), width = p.width();
        Queue<Integer> order = new Queue<>();
        int s = 0, t = height * width - 1;
        order.enqueue(s);
        for (int j = width - 2; j >= 1; j--)
        {
            int jj = j;
            int ii = 1;
            while (ii + 1 <= height - 1 && jj + 1 <= width - 1)
                order.enqueue(trans(ii++, jj++));
        }
        for (int i = 2; i < height - 1; i++)
        {
            int ii = i;
            int jj = 1;
            while (ii + 1 <= height - 1 && jj + 1 <= width - 1)
                order.enqueue(trans(ii++, jj++));
        }
        order.enqueue(t);
        
        return order;
    }
    
    private int trans(int i, int j)
    {
        return i * p.width() + j;
    }
    
    private void relax(int v, double[] distTo, int[] edgeTo, double[] energy)
    {
        int height = p.height(), width = p.width();
        if (v == 0)
        {
            for (int j = 1; j < width - 1; j++)
            {
                distTo[trans(1, j)] = energy[trans(1, j)];
                edgeTo[trans(1, j)] = 0;
            }
            return;
        }
        
        int t = width * height - 1;
        if (v == t)
            return;
        
        int j = v % width;
        int i = (v - j) / width;
        int left = trans(i+1, j-1), bottom = trans(i+1, j), right = trans(i+1, j+1);
        
        if (i == height - 2)
        {
            
            if (distTo[t] > distTo[v] + energy[t])
            {
                distTo[t] = distTo[v] + energy[t];
                edgeTo[t] = v;
            }
            return;
        }
        
        if (distTo[left] > distTo[v] + energy[left])
        {
            distTo[left] = distTo[v] + energy[left];
            edgeTo[left] = v;
        }
        if (distTo[bottom] > distTo[v] + energy[bottom])
        {
            distTo[bottom] = distTo[v] + energy[bottom];
            edgeTo[bottom] = v;
        }
        if (distTo[right] > distTo[v] + energy[right])
        {
            distTo[right] = distTo[v] + energy[right];
            edgeTo[right] = v;
        }
    }
    
    public void removeHorizontalSeam(int[] seam)
    {
        if (!callHorizontal)
        {
            p = transpose(p);
            removeSeam(seam);
            callHorizontal = true;
        }
        else removeSeam(seam);
    }
    
    public void removeVerticalSeam(int[] seam)
    {
        if (!callHorizontal) removeSeam(seam);
        else
        {
            p = transpose(p);
            removeSeam(seam);
            callHorizontal = false;
        }
    }
    
    private void removeSeam(int[] seam)
    {
        validatedSeam(seam);
        int height = p.height(), width = p.width();
        if (width <= 1) 
            throw new IllegalArgumentException("width or height is less than or equal to 1");
        
        Picture newP = new Picture(width-1, height);
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < seam[i]; j++)
                newP.setRGB(j, i, p.getRGB(j, i));
            for (int j = seam[i]; j < width-1; j++)
                newP.setRGB(j, i, p.getRGB(j+1, i));
        }
        p = newP;
    }
    
    private void validatedSeam(int[] seam)
    {
        if (seam == null) throw new IllegalArgumentException("argument is null");
        int len = seam.length;
        if (len != p.height())
            throw new IllegalArgumentException("seam is invalid: wrong length");
        
        for (int i = 0; i < len - 1; i++)
        {
            int v = seam[i], w = seam[i+1];
            if (Math.abs(v-w) > 1)
                throw new IllegalArgumentException("seam is invalid: two adjacent entries"
                        + "differ by more than 1");
        }
        
        for (int i = 0; i < len; i++)
        {
            int v = seam[i];
            if (v < 0 || v >= p.width())
                throw new IllegalArgumentException("seam is invalid: outside the prescribed range");
        }
    }
    
    public static void main(String[] args)
    {
        Picture p = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(p);
        for (int i : sc.findVerticalSeam())
            StdOut.print(i + " ");
        StdOut.println();
        for (int i : sc.findHorizontalSeam())
            StdOut.print(i + " ");
        StdOut.println();
        for (int i : sc.topological())
            StdOut.print(i + " ");
        StdOut.println();
    }
}