package com.localadmin.manager;

import android.os.Bundle;

interface IUserService {
    Bundle execute(String command) = 1;
    void destroy() = 16777114;
}
