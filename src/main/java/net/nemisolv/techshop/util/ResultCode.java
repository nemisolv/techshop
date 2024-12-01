package net.nemisolv.techshop.util;

public enum ResultCode {

    /**
     * Success status code
     */
    SUCCESS(200, "Success"),

    /**
     * Abnormal parameter
     */
    PARAMS_ERROR(4002, "Parameter exception"),

    METHOD_ARGUMENT_NOT_VALID(4001, "Method argument not valid"),

    ILLEGAL_ARGUMENT(4003, "Illegal argument"),

    RESOURCE_NOT_FOUND(4004, "Resource not found"),


    ERROR(400, "Server is busy, please try again later"),

    /**
     * User related errors
     */
    USER_SESSION_EXPIRED(20004, "User session has expired, please log in again"),
    USER_PERMISSION_ERROR(20005, "Insufficient permissions"),
    USER_PASSWORD_ERROR(20010, "Incorrect password"),
    USER_AUTH_ERROR(20005, "Authentication failed"),
    EMPLOYEE_NOT_FOUND(20027, "Employee does not exist"),
    EMPLOYEE_DISABLED(20031, "Employee has been disabled"),
    USER_NOT_FOUND(20002, "User does not exist or account is disabled"),
    USER_ALREADY_EXISTS(20003, "User already exists"),

    // Auth related errors
    AUTH_TOKEN_INVALID(20007, "Invalid token"),
    ROLE_NOT_FOUND(20008, "Role not found"),

    /**
     * Store related errors
     */
    STORE_NOT_FOUND(50001, "Store not found"),
    STORE_NAME_ALREADY_EXISTS(50002, "Store name already exists"),
    STORE_ALREADY_HAS_STORE(50003, "You already own a store"),
    STORE_NOT_OPENED(50004, "This member has not opened a store"),
    STORE_NOT_LOGGED_IN(50005, "Not logged in to store"),
    STORE_CLOSED(50006, "Store is closed, please contact the administrator"),
    STORE_DELIVER_PRODUCT_ADDRESS(50007, "Please provide the supplier's shipping address"),
    FREIGHT_TEMPLATE_NOT_FOUND(50010, "Current template does not exist"),
    STORE_STATUS_IN_PROGRESS(50011, "Store is in registration or approval process, please do not repeat the operation"),
    STORE_SHIPPING_ADDRESS_REQUIRED(50012, "Please provide a shipping address"),

    /**
     * Product related errors
     */
    PRODUCT_ERROR(11001, "Product error, please try again later"),
    PRODUCT_NOT_EXIST(11001, "Product is out of stock"),
    PRODUCT_NAME_ERROR(11002, "Product name is incorrect, name must be between 2-50 characters"),
    PRODUCT_UNDER_ERROR(11003, "Product shelf down failed"),
    PRODUCT_UPPER_ERROR(11004, "Product shelf up failed"),
    PRODUCT_AUTH_ERROR(11005, "Product approval failed"),
    POINT_PRODUCT_ERROR(11006, "Point accumulation product transaction error, please try again later"),
    POINT_PRODUCT_NOT_EXIST(11020, "Point accumulation product does not exist"),
    POINT_PRODUCT_CATEGORY_EXIST(11021, "Point accumulation product category already exists"),
    PRODUCT_SKU_SN_ERROR(11007, "Product SKU code cannot be empty"),
    PRODUCT_SKU_PRICE_ERROR(11008, "Product SKU price cannot be less than or equal to 0"),
    PRODUCT_SKU_COST_ERROR(11009, "Product SKU cost price cannot be less than or equal to 0"),
    PRODUCT_SKU_WEIGHT_ERROR(11010, "Product weight cannot be negative"),
    PRODUCT_SKU_QUANTITY_ERROR(11011, "Product stock quantity cannot be negative"),
    PRODUCT_SKU_QUANTITY_NOT_ENOUGH(11011, "Not enough stock quantity"),
    MUST_HAVE_PRODUCT_SKU(11012, "At least one SKU specification is required!"),
    MUST_HAVE_SALES_MODEL(11022, "If sales model is wholesale, wholesale rules are required!"),

    HAVE_INVALID_SALES_MODEL(11023, "Wholesale rule contains invalid data less than or equal to 0!"),
    MUST_HAVE_PRODUCT_SKU_VALUE(11024, "SKU specification value cannot be empty!"),
    DO_NOT_MATCH_WHOLESALE(11025, "Wholesale product purchase quantity cannot be lower than the starting quantity!"),
    PRODUCT_NOT_ERROR(11026, "Product does not exist"),

    PRODUCT_PARAMS_ERROR(11013, "Product specification error, please refresh and try again"),
    PHYSICAL_PRODUCT_NEED_TEMP(11014, "Physical product requires selecting a shipping template"),
    VIRTUAL_PRODUCT_NOT_NEED_TEMP(11015, "Virtual product does not need a shipping template"),
    PRODUCT_NOT_EXIST_STORE(11017, "Current user does not have permission to operate this product"),
    PRODUCT_TYPE_ERROR(11016, "Product type must be selected"),
    PRODUCT_STOCK_IMPORT_ERROR(11018, "Product stock import failed, please check the data in the table"),

    /**
     * Brand related errors
     */
    PRODUCT_BRAND_SAVE_ERROR(14001, "Add brand failed"),
    PRODUCT_BRAND_UPDATE_ERROR(14002, "Update brand failed"),
    PRODUCT_BRAND_DISABLE_ERROR(14003, "Disable brand failed"),
    PRODUCT_BRAND_DELETE_ERROR(14004, "Delete brand failed"),
    PRODUCT_BRAND_NAME_EXIST_ERROR(20002, "Brand name already exists!"),
    PRODUCT_BRAND_USE_DISABLE_ERROR(20003, "Category is linked to the brand, please unlink it first"),
    PRODUCT_BRAND_BIND_ERROR(20005, "Brand is linked to products, please unlink it first"),
    PRODUCT_BRAND_NOT_EXIST(20004, "Brand does not exist"),

    /**
     * Specification related errors
     */
    PRODUCT_SPEC_SAVE_ERROR(13001, "Save specification failed"),
    PRODUCT_SPEC_UPDATE_ERROR(13002, "Update specification failed"),
    PRODUCT_SPEC_DELETE_ERROR(13003, "Category is linked with this specification, please unlink it first"),

    /**
     * Category related errors
     */
    PRODUCT_CATEGORY_NOT_EXIST(10001, "Product category does not exist"),
    PRODUCT_CATEGORY_NAME_IS_EXIST(10002, "Category name already exists"),
    PRODUCT_CATEGORY_PARENT_NOT_EXIST(10003, "Parent category does not exist"),
    PRODUCT_CATEGORY_BEYOND_THREE(10004, "Maximum 3 levels of categories, add failed"),
    PRODUCT_CATEGORY_HAS_CHILDREN(10005, "This category contains subcategories, cannot be deleted"),
    CATEGORY_HAS_PRODUCT(10006, "This category contains products, cannot be deleted"),
    PRODUCT_CATEGORY_SAVE_ERROR(10007, "Category cannot be saved, it contains products"),
    PRODUCT_CATEGORY_PARAMETER_NOT_EXIST(10012, "Linked parameter group for the category does not exist"),
    PRODUCT_CATEGORY_PARAMETER_SAVE_ERROR(10008, "Failed to add linked parameter group for the category"),
    PRODUCT_CATEGORY_PARAMETER_UPDATE_ERROR(10009, "Failed to update linked parameter group for the category"),
    PRODUCT_CATEGORY_DELETE_FLAG_ERROR(10010, "Subcategory status cannot differ from parent category status!"),
    PRODUCT_CATEGORY_COMMISSION_RATE_ERROR(10011, "Category commission rate is incorrect!"),

    /**
     * Parameter related errors
     */
    PRODUCT_PARAMETER_SAVE_ERROR(12001, "Add parameter failed"),
    PRODUCT_PARAMETER_UPDATE_ERROR(12002, "Update parameter failed"),

    /**
     * System exceptions
     */
    RATE_LIMIT_ERROR(1003, "Access too frequent, please try again later"),
    INTERNAL_SERVER_ERROR(1004, "Server is busy, please try again later"),
    ;

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
