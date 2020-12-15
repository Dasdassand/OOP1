package logics;

import java.util.ArrayList;
import java.util.List;

class CircularList<T> {
    private List<T> list = new ArrayList<>();

    void add(T num){
        list.add(num);
    }
    void add(int index,T num){
        list.add(index,num);
    }
    T remove(int index){
        return list.remove(index);
    }

    T get(int i){
        if(i > list.size() - 1)
            return get(i - list.size());
        else if (i < 0)
            return get(i + list.size());
        else
            return list.get(i);
    }

    int getNumber(T data){
        if (contains(data))
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(data))
                    return i;
            }
        return 0;
    }

    boolean contains(T data){
        for (T t : list) {
            if (t.equals(data))
                return true;
        }
        return false;
    }

    int indexOf(T data){
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).equals(data))
                return i;
        }
        return -1;
    }

    int size(){
        return list.size();
    }

    void clear(){
        list.clear();
    }

    List<T> toList(){
        return list;
    }
}
