/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.TagDTO;
import java.util.concurrent.Callable;

class GetTagDTO implements Callable<TagDTO> {

    String url;

    GetTagDTO(String url) {
        this.url = url;
    }

    @Override
    public TagDTO call() throws Exception {
        TagCounter tc = new TagCounter(url);
        tc.doWork();
        return new TagDTO(tc);
    }
}