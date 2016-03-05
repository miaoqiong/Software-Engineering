package entity;

import java.util.ArrayList;

/**
 * TopkUsageResult records the details of Top-k usage result details. 
 * 
 */
public class TopkUsageResult {

    private ArrayList<String> resultName;
    private Long usageTime;
    /**
     * Construct a TopkUsageResult object with attribute set to given parameter
     * @param resultName required result name
     * @param usageTime total usage time
     */
    public TopkUsageResult(ArrayList<String> resultName, Long usageTime) {
        this.resultName = resultName;
        this.usageTime = usageTime;
    }
    
    /**
     * Get total usage time
     * @return total usage time
     */
    public Long getUsageTime() {
        return usageTime;
    }
    
    /**
     * Get result names
     * @return ArrayList of result names
     */
    public ArrayList<String> getResultName() {
        return resultName;
    }
    
    /**
     * Add new name into result names
     * @param name new name
     */
    public void addResultName(String name) {
        resultName.add(name);
    }
    
}
