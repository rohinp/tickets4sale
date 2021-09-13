package com.rohin.tickets4sale.core.errors

enum ErrorType:
  case Database
  case Parsing
  case API
  case Unknown

enum SimpleError(msg:String, errorType:ErrorType) extends Exception(msg):
  case PerformanceDecodeFailure(msg:String) extends SimpleError(msg, ErrorType.Parsing)
  case RawPerformanceDecodeFailure(msg:String) extends SimpleError(msg, ErrorType.Parsing)
  