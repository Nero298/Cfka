package com.zodiactap.mapper.sysbridge;

interface IShizukuStarterService {
    void destroy() = 16777114; // Destroy method defined by Shizuku server

    String executeCommand(String command) = 1;
}