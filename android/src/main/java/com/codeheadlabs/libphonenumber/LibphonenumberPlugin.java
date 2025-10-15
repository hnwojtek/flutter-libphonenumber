package com.codeheadlabs.libphonenumber;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LibphonenumberPlugin implements FlutterPlugin, MethodCallHandler {
  private MethodChannel channel;
  private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
  private static final PhoneNumberToCarrierMapper phoneNumberToCarrierMapper = PhoneNumberToCarrierMapper.getInstance();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    channel = new MethodChannel(binding.getBinaryMessenger(), "codeheadlabs.com/libphonenumber");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (channel != null) {
      channel.setMethodCallHandler(null);
      channel = null;
    }
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "isValidPhoneNumber":
        handleIsValidPhoneNumber(call, result);
        break;
      case "normalizePhoneNumber":
        handleNormalizePhoneNumber(call, result);
        break;
      case "getRegionInfo":
        handleGetRegionInfo(call, result);
        break;
      case "getNumberType":
        handleGetNumberType(call, result);
        break;
      case "getExampleNumber":
        handleGetExampleNumber(call, result);
        break;
      case "formatAsYouType":
        formatAsYouType(call, result);
        break;
      case "getNameForNumber":
        handleGetNameForNumber(call, result);
        break;
      case "format":
        handleFormat(call, result);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private void handleGetNameForNumber(MethodCall call, Result result) {
    final String phoneNumber = call.argument("phone_number");
    final String isoCode = call.argument("iso_code");

    try {
      Phonenumber.PhoneNumber p = phoneUtil.parse(phoneNumber, isoCode.toUpperCase());
      result.success(phoneNumberToCarrierMapper.getNameForNumber(p, Locale.getDefault()));
    } catch (NumberParseException e) {
      result.error("NumberParseException", e.getMessage(), null);
    }
  }

  private void handleFormat(MethodCall call, Result result) {
    final String phoneNumber = call.argument("phone_number");
    final String isoCode = call.argument("iso_code");
    final String format = call.argument("format");

    try {
      Phonenumber.PhoneNumber p = phoneUtil.parse(phoneNumber, isoCode.toUpperCase());
      PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.valueOf(format);
      result.success(phoneUtil.format(p, phoneNumberFormat));
    } catch (Exception e) {
      result.error("Exception", e.getMessage(), null);
    }
  }

  private void handleIsValidPhoneNumber(MethodCall call, Result result) {
    final String phoneNumber = call.argument("phone_number");
    final String isoCode = call.argument("iso_code");

    try {
      Phonenumber.PhoneNumber p = phoneUtil.parse(phoneNumber, isoCode.toUpperCase());
      result.success(phoneUtil.isValidNumber(p));
    } catch (NumberParseException e) {
      result.error("NumberParseException", e.getMessage(), null);
    }
  }

  private void handleNormalizePhoneNumber(MethodCall call, Result result) {
    final String phoneNumber = call.argument("phone_number");
    final String isoCode = call.argument("iso_code");

    try {
      Phonenumber.PhoneNumber p = phoneUtil.parse(phoneNumber, isoCode.toUpperCase());
      final String normalized = phoneUtil.format(p, PhoneNumberUtil.PhoneNumberFormat.E164);
      result.success(normalized);
    } catch (NumberParseException e) {
      result.error("NumberParseException", e.getMessage(), null);
    }
  }

  private void handleGetRegionInfo(MethodCall call, Result result) {
    final String phoneNumber = call.argument("phone_number");
    final String isoCode = call.argument("iso_code");

    try {
      Phonenumber.PhoneNumber p = phoneUtil.parse(phoneNumber, isoCode.toUpperCase());
      String regionCode = phoneUtil.getRegionCodeForNumber(p);
      String countryCode = String.valueOf(p.getCountryCode());
      String formattedNumber = phoneUtil.format(p, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

      Map<String, String> resultMap = new HashMap<>();
      resultMap.put("isoCode", regionCode);
      resultMap.put("regionCode", countryCode);
      resultMap.put("formattedPhoneNumber", formattedNumber);
      result.success(resultMap);
    } catch (NumberParseException e) {
      result.error("NumberParseException", e.getMessage(), null);
    }
  }

  private void handleGetExampleNumber(MethodCall call, Result result) {
    final String isoCode = call.argument("iso_code");
    Phonenumber.PhoneNumber p = phoneUtil.getExampleNumber(isoCode);
    String regionCode = phoneUtil.getRegionCodeForNumber(p);
    String formattedNumber = phoneUtil.format(p, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

    Map<String, String> resultMap = new HashMap<>();
    resultMap.put("isoCode", regionCode);
    resultMap.put("formattedPhoneNumber", formattedNumber);
    result.success(resultMap);
  }

 private void handleGetNumberType(MethodCall call, Result result) {
  final String phoneNumber = call.argument("phone_number");
  final String isoCode = call.argument("iso_code");

  try {
    Phonenumber.PhoneNumber p = phoneUtil.parse(phoneNumber, isoCode.toUpperCase());
    PhoneNumberUtil.PhoneNumberType t = phoneUtil.getNumberType(p);

    int typeCode;
    switch (t) {
      case FIXED_LINE:
        typeCode = 0;
        break;
      case MOBILE:
        typeCode = 1;
        break;
      case FIXED_LINE_OR_MOBILE:
        typeCode = 2;
        break;
      case TOLL_FREE:
        typeCode = 3;
        break;
      case PREMIUM_RATE:
        typeCode = 4;
        break;
      case SHARED_COST:
        typeCode = 5;
        break;
      case VOIP:
        typeCode = 6;
        break;
      case PERSONAL_NUMBER:
        typeCode = 7;
        break;
      case PAGER:
        typeCode = 8;
        break;
      case UAN:
        typeCode = 9;
        break;
      case VOICEMAIL:
        typeCode = 10;
        break;
      case UNKNOWN:
      default:
        typeCode = -1;
        break;
    }

    result.success(typeCode);
  } catch (NumberParseException e) {
    result.error("NumberParseException", e.getMessage(), null);
  }
}

  private void formatAsYouType(MethodCall call, Result result) {
    final String phoneNumber = call.argument("phone_number");
    final String isoCode = call.argument("iso_code");

    AsYouTypeFormatter asYouTypeFormatter = phoneUtil.getAsYouTypeFormatter(isoCode.toUpperCase());
    String res = null;
    for (int i = 0; i < phoneNumber.length(); i++) {
      res = asYouTypeFormatter.inputDigit(phoneNumber.charAt(i));
    }
    result.success(res);
  }
}
