package cc.colorcat.vangoghdemo.internal;

/**
 * Created by cxx on 2017/8/15.
 * xx.ch@outlook.com
 */
public interface PermissionListener {

    /**
     * 当且仅当请求的所有权限都被授予后才会被调用
     */
    void onAllGranted();

    /**
     * 有权限未被授予时会被调用
     *
     * @param permissions 所有未被授予的权限
     */
    void onDenied(String[] permissions);
}
