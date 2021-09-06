package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import com.mobiquity.service.BuildPackItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Packer {

  private Packer() {
  }

  @GetMapping("/pack")
  public static String pack(@RequestParam(value="path")String filePath) throws APIException {
    return BuildPackItem.getFileContent(filePath);
  }

}

