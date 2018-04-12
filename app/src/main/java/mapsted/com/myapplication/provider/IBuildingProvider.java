package mapsted.com.myapplication.provider;


public interface IBuildingProvider<T> {
    T getById(int id);
}
