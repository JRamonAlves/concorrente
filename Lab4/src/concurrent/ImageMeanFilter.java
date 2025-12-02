import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

/**
 * This class provides functionality to apply a mean filter to an image.
 * The mean filter is used to smooth images by averaging the pixel values
 * in a neighborhood defined by a kernel size.
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * ImageMeanFilter.applyMeanFilter("input.jpg", "output.jpg", 3);
 * }
 * </pre>
 * 
 * <p>Supported image formats: JPG, PNG</p>
 * 
 * <p>Author: temmanuel@comptuacao.ufcg.edu.br</p>
 */
public class ImageMeanFilter {

    /**
     * Main method for demonstration
     * 
     * Usage: java ImageMeanFilter <input_file>
     * 
     * Arguments:
     *   input_file - Path to the input image file to be processed
     *                Supported formats: JPG, PNG
     * 
     * Example:
     *   java ImageMeanFilter input.jpg
     * 
     * The program will generate a filtered output image named "filtered_output.jpg"
     * using a 7x7 mean filter kernel
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java ImageMeanFilter <input_file>");
            System.exit(1);
        }

        String inputPath = args[0];
        int nThreads = Integer.parseInt(args[1]);

        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File(inputPath));
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
            
        BufferedImage filteredImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );

        int linhasPorThread = originalImage.getHeight() / nThreads;
        int restoLinhas = originalImage.getHeight() % nThreads;

        Counter filtrados = new Counter();
        Counter iguais = new Counter();

        
        Thread[] threads = new Thread[nThreads];
        threads[0] = new Thread(new AppliesMeanFilter(0, linhasPorThread + restoLinhas, originalImage, filteredImage, filtrados, iguais));
        threads[0].start();

        int lastLine = linhasPorThread + restoLinhas;
        for (int i = 1; i < nThreads; i++) {
            threads[i] = new Thread(new AppliesMeanFilter(lastLine, lastLine + linhasPorThread, originalImage, filteredImage, filtrados, iguais));
            threads[i].start();
            lastLine += linhasPorThread;
        }

        for (int i = 0; i < nThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            ImageIO.write(filteredImage, "jpg", new File("filtered_output.jpg"));
        } catch (IOException e) {            
            System.err.println("Error processing image: " + e.getMessage());
        }

        System.out.println("Celulas afetadas: " + filtrados.pegaCounta());
        System.out.println("Celulas não afetadas: " + iguais.pegaCounta());
        
    }
}











class AppliesMeanFilter implements Runnable{

    private int start, end;
    private BufferedImage originalImage, filtredImage; 
    private Counter filtrados;
    private Counter iguais;


    public AppliesMeanFilter(int start, int end, BufferedImage originalImage, BufferedImage filteredImage, Counter filtrados, Counter iguais) {
        this.end = end;
        this.start = start;
        this.filtredImage = filteredImage;
        this.originalImage = originalImage;
        this.filtrados = filtrados;
        this.iguais = iguais;
    }   

    @Override
    public void run() {
        applyMeanFilter(originalImage, filtredImage, start, end);
    }
    

    private int[] calculateNeighborhoodAverage(BufferedImage image, int centerX, int centerY, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pad = kernelSize / 2;
        
        // Arrays for color sums
        long redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = 0;
        
        // Process neighborhood
        for (int dy = -pad; dy <= pad; dy++) {
            for (int dx = -pad; dx <= pad; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                // Check image bounds
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    // Get pixel color
                    int rgb = image.getRGB(x, y);
                    
                    // Extract color components
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    // Sum colors
                    redSum += red;
                    greenSum += green;
                    blueSum += blue;
                    pixelCount++;
                }
            }
        }

        int rgb = image.getRGB(centerX, centerY);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        int[] out = new int[] {
            (int)(redSum / pixelCount),
            (int)(greenSum / pixelCount),
            (int)(blueSum / pixelCount)
        };

        if (red != out[0] || green != out[1] || blue != out[2]) {
            this.filtrados.incrementa();
        } else {
            this.iguais.incrementa();
        }

        return out;
    }

    public void applyMeanFilter(BufferedImage originalImage, BufferedImage filteredImage,int start, int end) {
    
        // Image processing
        int width = originalImage.getWidth();
        // Process each pixel
        for (int y = start; y < end; y++) {
            for (int x = 0; x < width; x++) {
                // Calculate neighborhood average
                int[] avgColor = calculateNeighborhoodAverage(originalImage, x, y, 7);
                
                // Set filtered pixel
                filteredImage.setRGB(x, y, 
                    (avgColor[0] << 16) | 
                    (avgColor[1] << 8)  | 
                    avgColor[2]
                );
            }
        }
    }

}

class Counter {
    private int counta = 0;
    Semaphore mutex = new Semaphore(1);

    public void incrementa() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.counta++;
        mutex.release();
    }

    public int pegaCounta() {
        return this.counta;
    }
}