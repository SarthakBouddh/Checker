package com.example.testing.dto;

public class DistanceResult {

    public int row;
    public int start;
    public int end;
    public int distance;
    public String status;

    public DistanceResult(int row, int start, int end, int distance, String status) {
        this.row = row;
        this.start = start;
        this.end = end;
        this.distance = distance;
        this.status = status;
    }
}
